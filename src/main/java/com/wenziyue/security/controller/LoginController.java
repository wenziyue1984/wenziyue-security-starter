package com.wenziyue.security.controller;


import com.wenziyue.security.model.LoginRequest;
import com.wenziyue.security.model.LoginResponse;
import com.wenziyue.security.properties.SecurityProperties;
import com.wenziyue.security.service.LoginService;
import com.wenziyue.security.service.RefreshCacheByRefreshToken;
import com.wenziyue.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wenziyue
 */

@RestController
@RequestMapping("/login")
@ConditionalOnProperty(name = "wenziyue.security.default-login-enabled", havingValue = "true", matchIfMissing = true)
public class LoginController {

    private final LoginService loginService;
    private final JwtUtils jwtUtils;
    private final SecurityProperties securityProperties;
    private final RefreshCacheByRefreshToken refreshCacheByRefreshToken;

    public LoginController(LoginService loginService,
                           JwtUtils jwtUtils,
                           SecurityProperties securityProperties,
                           @Nullable RefreshCacheByRefreshToken refreshCacheByRefreshToken) {
        this.loginService = loginService;
        this.jwtUtils = jwtUtils;
        this.securityProperties = securityProperties;
        this.refreshCacheByRefreshToken = refreshCacheByRefreshToken;
    }

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest request) {
        String token = loginService.login(request);
        return new LoginResponse(token);
    }

    @PostMapping("/refresh")
    public String refreshToken(HttpServletRequest request) {
        // 从请求头中拿 token
        String authHeader = request.getHeader(securityProperties.getTokenHeader());
        String tokenPrefix = securityProperties.getTokenPrefix();

        if (authHeader == null || !authHeader.startsWith(tokenPrefix)) {
            throw new RuntimeException("无效的 token");
        }

        String token = authHeader.substring(tokenPrefix.length());

        // 校验 token 是否过期
        if (jwtUtils.isTokenExpired(token)) {
            throw new RuntimeException("Token 已过期，不能刷新，请重新登录");
        }

        // 获取 userId 并生成新 token
        String userId = jwtUtils.getUserIdFromToken(token);
        String newToken = jwtUtils.generateToken(userId);

        // 更新缓存（如果有实现的话）
        if (refreshCacheByRefreshToken != null) {
            refreshCacheByRefreshToken.refreshCacheByRefreshToken(token);
        }

        return newToken;
    }
}
