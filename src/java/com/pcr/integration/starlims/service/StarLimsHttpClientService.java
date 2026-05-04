package com.pcr.integration.starlims.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pcr.integration.starlims.config.StarLimsProperties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class StarLimsHttpClientService {

    private static final String STAMP = "4b535cfa149d62f0b0bfd16f12e9ace67acaf9f3";
    private static final String STAMP1 = "ok";
    private static final String WEB_SITE_NAME = "/FDD";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final String USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                    + "(KHTML, like Gecko) Chrome/147.0.0.0 Safari/537.36 Edg/147.0.0.0";

    private final StarLimsProperties properties;
    private final ObjectMapper objectMapper;
    private final CookieManager cookieManager;
    private final HttpClient httpClient;

    private String lastDigest;
    private String dept = "";
    private String role = "";
    private String fullname = "";
    private LocalDateTime lastLoginTime;

    public StarLimsHttpClientService(StarLimsProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.cookieManager = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .cookieHandler(cookieManager)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public synchronized Map<String, Object> loginByHttp() {
        clearSession();

        try {
            HttpResponse<String> startResponse = rawGet("/starthtml.lims", false);
            updateDigest(startResponse);
            ensureOk(startResponse.statusCode(), startResponse.body(), "GET starthtml.lims failed");

            postText("Authentication.SetUserTimeZone", new Object[]{"UtcOffset|8:00", "UtcOffset|8:00"});

            String username = normalizePlainValue(properties.getUsername());
            String password = normalizePlainValue(properties.getPassword());
            String hexUser = encodeHex(username);
            String hexPass = encodeHex(password);

            postText("Authentication.GetLicenseTypeForUser", new Object[]{hexUser});

            JsonNode roleInfo = postJson(
                    "Authentication.HTML_GetSiteRoleViewInfo",
                    new Object[]{hexUser, hexPass, null, null, null}
            );
            fillRoleInfo(roleInfo);

            String sessionToken = postText("Authentication.ResetSessionId", new Object[]{});
            postText("Authentication.SetVars", new Object[]{"", "", trimQuotes(sessionToken)});

            String loginResult = postText(
                    "Authentication.LoginMobile",
                    new Object[]{buildLoginBody(hexUser, hexPass)}
            );

            if (!loginResult.contains("LoadMainContainer")) {
                throw new RuntimeException("STARLIMS 登录失败: " + abbreviate(loginResult, 300));
            }

            this.lastLoginTime = LocalDateTime.now();

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("message", "HTTP login success");
            result.put("loginTime", lastLoginTime.toString());
            result.put("baseUrl", getFddBaseUrl());
            result.put("username", username);
            result.put("dept", dept);
            result.put("role", role);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("HTTP 登录 STARLIMS 失败", e);
        } catch (IOException e) {
            throw new RuntimeException("HTTP 登录 STARLIMS 失败", e);
        }
    }

    public synchronized void ensureLoggedIn() {
        if (lastLoginTime == null || lastLoginTime.plusMinutes(20).isBefore(LocalDateTime.now())) {
            loginByHttp();
        }
    }

    public synchronized JsonNode postJson(String scriptName, Object params) {
        return parseJson(postText(scriptName, params));
    }

    public synchronized String postText(String scriptName, Object params) {
        try {
            String json = objectMapper.writeValueAsString(params);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getFddBaseUrl() + "/" + scriptName + ".lims"))
                    .timeout(TIMEOUT)
                    .header("User-Agent", USER_AGENT)
                    .header("runtimecalltype", "HTML/Mobile/w")
                    .header("cache-control", "no-cache")
                    .header("x-requested-with", "XMLHttpRequest")
                    .header("content-type", "application/json")
                    .header("referer", getFddBaseUrl() + "/starthtml.lims")
                    .header("cacheditemdigest", signPost(scriptName, json))
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );
            updateDigest(response);
            ensureOk(response.statusCode(), response.body(), "POST " + scriptName + " failed");
            return response.body();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("STARLIMS 请求序列化失败: " + scriptName, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("STARLIMS POST 请求失败: " + scriptName, e);
        } catch (IOException e) {
            throw new RuntimeException("STARLIMS POST 请求失败: " + scriptName, e);
        }
    }

    public synchronized JsonNode getJson(String relativePath) {
        return parseJson(getText(relativePath));
    }

    public synchronized String getText(String relativePath) {
        try {
            HttpResponse<String> response = rawGet(relativePath, true);
            updateDigest(response);
            ensureOk(response.statusCode(), response.body(), "GET " + relativePath + " failed");
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("STARLIMS GET 请求失败: " + relativePath, e);
        } catch (IOException e) {
            throw new RuntimeException("STARLIMS GET 请求失败: " + relativePath, e);
        }
    }

    public synchronized byte[] getBytesAbsolute(String absoluteUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(absoluteUrl))
                    .timeout(TIMEOUT)
                    .header("User-Agent", USER_AGENT)
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            ensureOkBytes(response.statusCode(), response.body(), "GET " + absoluteUrl + " failed");
            return response.body();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("STARLIMS 下载失败: " + absoluteUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("STARLIMS 下载失败: " + absoluteUrl, e);
        }
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public String getFddBaseUrl() {
        String baseUrl = properties.getBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalStateException("starlims.base-url 未配置");
        }
        String trimmed = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        return trimmed.endsWith("/FDD") ? trimmed : trimmed + "/FDD";
    }

    public String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private HttpResponse<String> rawGet(String relativePath, boolean signed) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(getFddBaseUrl() + ensureSlash(relativePath)))
                .timeout(TIMEOUT)
                .header("User-Agent", USER_AGENT)
                .header("cache-control", "no-cache")
                .header("x-requested-with", "XMLHttpRequest")
                .header("referer", getFddBaseUrl() + "/starthtml.lims")
                .GET();

        if (signed) {
            builder.header("runtimecalltype", "HTML/Mobile/h")
                    .header("cacheditemdigest", signGet(relativePath))
                    .header("no-cache", String.valueOf(System.currentTimeMillis()));
        }

        return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private void fillRoleInfo(JsonNode roleInfo) {
        ArrayDeque<JsonNode> stack = new ArrayDeque<>();
        if (roleInfo != null && !roleInfo.isNull()) {
            stack.push(roleInfo);
        }

        while (!stack.isEmpty()) {
            JsonNode current = stack.pop();
            if (current.isArray()) {
                Iterator<JsonNode> iterator = current.elements();
                while (iterator.hasNext()) {
                    stack.push(iterator.next());
                }
                continue;
            }

            if (!current.isTextual()) {
                continue;
            }

            String value = current.asText("");
            if (value.startsWith("FQT")) {
                dept = value;
                continue;
            }
            if ("Active".equalsIgnoreCase(value) || value.length() < 2) {
                continue;
            }
            if (value.contains("检测")
                    || value.contains("组长")
                    || value.contains("任务")
                    || value.contains("数据")
                    || value.toLowerCase().contains("analyst")
                    || value.toLowerCase().contains("group")) {
                role = value;
                continue;
            }
            if (fullname == null || fullname.isBlank()) {
                fullname = value;
            }
        }
    }

    private LoginBody buildLoginBody(String hexUser, String hexPass) {
        LoginBody body = new LoginBody();
        body.username = hexUser;
        body.password = hexPass;
        body.dept = isBlank(dept) ? "FQT14" : dept;
        body.role = isBlank(role) ? "检测人员" : role;
        body.fullname = fullname == null ? "" : fullname;
        body.platforma = "HTML";
        body.no_c = System.currentTimeMillis();
        return body;
    }

    private JsonNode parseJson(String body) {
        try {
            return objectMapper.readTree(body);
        } catch (IOException e) {
            throw new RuntimeException("STARLIMS 返回的不是合法 JSON: " + abbreviate(body, 300), e);
        }
    }

    private void updateDigest(HttpResponse<?> response) {
        String digest = response.headers().firstValue("digest").orElse(null);
        if (digest != null && !digest.isBlank()) {
            this.lastDigest = digest;
        }
    }

    private void clearSession() {
        cookieManager.getCookieStore().removeAll();
        lastDigest = null;
        dept = "";
        role = "";
        fullname = "";
        lastLoginTime = null;
    }

    private String signPost(String scriptName, String paramsJson) {
        String requestPackage = scriptName + "\n" + paramsJson + STAMP1;
        String requestHash = sha1Hex(requestPackage);
        String responseHash = STAMP;
        String crc = sha1Hex(requestHash + responseHash).substring(0, 8);
        return requestHash + responseHash + crc;
    }

    private String signGet(String path) {
        String fullUrl = path.startsWith("/") ? WEB_SITE_NAME + path : WEB_SITE_NAME + "/" + path;
        String requestHash = sha1Hex(fullUrl + STAMP + STAMP1);
        String responseHash = isBlank(lastDigest) ? STAMP : lastDigest;
        String crc = sha1Hex(requestHash + responseHash).substring(0, 8);
        return requestHash + responseHash + crc;
    }

    private static String normalizePlainValue(String value) {
        return value == null ? "" : value.trim();
    }

    public static String encodeHex(String text) {
        StringBuilder builder = new StringBuilder();
        for (char ch : text.toCharArray()) {
            builder.append(String.format("%04X", (int) ch));
        }
        return builder.toString();
    }

    private static String sha1Hex(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte b : hash) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("缺少 SHA-1 算法支持", e);
        }
    }

    private static String trimQuotes(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() >= 2 && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }
        return trimmed;
    }

    private static String ensureSlash(String path) {
        return path.startsWith("/") ? path : "/" + path;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String abbreviate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }

    private static void ensureOk(int statusCode, String body, String message) {
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }
        throw new RuntimeException(message + ", status=" + statusCode + ", body=" + abbreviate(body, 200));
    }

    private static void ensureOkBytes(int statusCode, byte[] body, String message) {
        if (statusCode >= 200 && statusCode < 300) {
            return;
        }
        String preview = body == null ? "" : new String(body, StandardCharsets.UTF_8);
        throw new RuntimeException(message + ", status=" + statusCode + ", body=" + abbreviate(preview, 200));
    }

    private static final class LoginBody {
        public String username;
        public String password;
        public String dept;
        public String role;
        public String fullname;
        public String platforma;
        public long no_c;
    }
}
