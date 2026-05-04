package com.example.demo;

import com.example.demo.user.dto.RegisterRequest;
import com.example.demo.user.entity.UserAccount;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class UserController {
    private static final int SESSION_TIMEOUT_SECONDS = 8 * 60 * 60;
    public static Map<String, String> loggedInUsers = new ConcurrentHashMap<>();
    public static String deleteExistingusername = null;
    public static String deleteExistingSeeionID = null;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public UserController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    // legacy endpoint for existing template pages
    @PostMapping("/login")
    public Map<String, String> login(@RequestParam("username") String username,
                                     @RequestParam("password") String password,
                                     HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        HttpSession session = request.getSession();
        session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        String currentSessionId = session.getId();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            session.setAttribute("username", username);
            session.setAttribute("password", password);
            loggedInUsers.put(username, currentSessionId);

            result.put("status", "已登录");
            result.put("username", username);
            return result;
        } catch (BadCredentialsException e) {
            result.put("status", "密码错误");
            return result;
        } catch (LockedException e) {
            result.put("status", "账号已锁定");
            return result;
        } catch (DisabledException e) {
            result.put("status", "账号已禁用");
            return result;
        } catch (Exception e) {
            result.put("status", "系统错误");
            return result;
        }
    }

    // legacy endpoint for existing template pages
    @PostMapping("/register")
    public Map<String, String> register(@RequestParam("username") String username,
                                        @RequestParam("password") String password) {
        Map<String, String> result = new HashMap<>();
        try {
            RegisterRequest request = new RegisterRequest();
            request.setUsername(username);
            request.setPassword(password);
            request.setFullName(username);
            UserAccount user = userService.register(request);
            result.put("status", user != null ? "注册成功" : "系统错误");
            return result;
        } catch (IllegalArgumentException e) {
            result.put("status", e.getMessage());
            return result;
        } catch (Exception e) {
            result.put("status", "系统错误");
            return result;
        }
    }

    // legacy endpoint for existing template pages
    @PostMapping("/logout")
    public Map<String, String> logout(String deleteusername, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Map<String, String> result = new HashMap<>();
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null) {
                removeLoggedInUser(username, session.getId());
            }
            session.invalidate();
        }
        result.put("status", "已退出");
        return result;
    }

    public static void removeLoggedInUser(String username) {
        loggedInUsers.remove(username);
    }

    public static void removeLoggedInUser(String username, String sessionId) {
        if (username == null || sessionId == null) {
            return;
        }
        loggedInUsers.remove(username, sessionId);
    }
}
