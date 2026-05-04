package com.example.demo.user.controller;

import com.example.demo.UserController;
import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.RegisterRequest;
import com.example.demo.user.entity.UserAccount;
import com.example.demo.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final int SESSION_TIMEOUT_SECONDS = 8 * 60 * 60;

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        UserAccount user = userService.register(request);
        return ResponseEntity.ok(Map.of(
                "status", "registered",
                "message", "注册成功",
                "user", userService.toResponse(user)
        ));
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> registerForm(@RequestParam("username") String username,
                                          @RequestParam("password") String password) {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setFullName(username);
        return register(request);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            String loginUsername = request.getUsername() == null ? "" : request.getUsername().trim();
            HttpSession preSession = httpRequest.getSession(true);
            preSession.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUsername, request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            httpRequest.getSession(true).setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext()
            );

            UserAccount user = (UserAccount) authentication.getPrincipal();
            HttpSession session = httpRequest.getSession();
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
            session.setAttribute("username", user.getUsername());
            session.setAttribute("password", request.getPassword());
            UserController.loggedInUsers.put(user.getUsername(), session.getId());

            return ResponseEntity.ok(Map.of(
                    "status", "logged_in",
                    "message", "登录成功",
                    "user", userService.toResponse(user)
            ));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("message", "用户名或密码错误"));
        } catch (LockedException e) {
            return ResponseEntity.status(401).body(Map.of("message", "账号已锁定"));
        } catch (DisabledException e) {
            return ResponseEntity.status(401).body(Map.of("message", "账号已禁用"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "登录失败: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> loginForm(@RequestParam("username") String username,
                                       @RequestParam("password") String password,
                                       HttpServletRequest httpRequest) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        return login(request, httpRequest);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(Map.of("message", "未登录或登录已失效"));
        }
        UserAccount user = userService.findByUsername(principal.getName());
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("message", "用户不存在"));
        }
        return ResponseEntity.ok(userService.toResponse(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    @RequestParam(value = "deleteusername", required = false) String deleteusername) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String username = (String) session.getAttribute("username");
                if (username != null) {
                    UserController.removeLoggedInUser(username, session.getId());
                }
            }
            request.logout();
        } catch (Exception ignored) {
        }
        try {
            request.getSession().invalidate();
        } catch (Exception ignored) {
        }
        return ResponseEntity.ok(Map.of("message", "退出成功"));
    }
}
