package com.wenziyue.security.config;

import com.wenziyue.security.controller.LoginController;
import com.wenziyue.security.properties.SecurityProperties;
import com.wenziyue.security.service.LoginService;
import com.wenziyue.security.service.RefreshCacheByRefreshToken;
import com.wenziyue.security.service.UserDetailsServiceByIdOrToken;
import com.wenziyue.security.service.impl.DefaultLoginService;
import com.wenziyue.security.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.util.Collections;

/**
 * @author wenziyue
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableWebSecurity
public class WenziyueSecurityAutoConfiguration {

    @Bean
    public JwtUtils jwtUtils(SecurityProperties properties) {
        return new JwtUtils(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsServiceByIdOrToken defaultUserDetailsServiceById() {
        return (userId, token) -> {
            log.info("UserDetailsServiceById默认实现，只返回 userId，无权限信息");
            // 默认实现只返回 userId，无权限信息
            return new org.springframework.security.core.userdetails.User(
                    String.valueOf(userId),
                    "",
                    Collections.emptyList()
            );
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RefreshCacheByRefreshToken defaultRefreshCacheByRefreshToken() {
        return (oldToken, newToken) -> log.info("RefreshCacheByRefreshToken默认实现");
    }

    @Bean
    @ConditionalOnMissingBean(name = "loginController") //只能
    @ConditionalOnProperty(prefix = "wenziyue.security", name   = "default-login-enabled", havingValue = "true", matchIfMissing = false)
    public LoginController loginController(
            LoginService loginService,
            JwtUtils jwtUtils,
            SecurityProperties securityProperties,
            ObjectProvider<RefreshCacheByRefreshToken> refreshCacheByRefreshTokenProvider) {
        return new LoginController(loginService, jwtUtils, securityProperties, refreshCacheByRefreshTokenProvider.getIfAvailable());
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityConfig securityConfig(
            SecurityProperties securityProperties, JwtUtils jwtUtils,
            UserDetailsServiceByIdOrToken userDetailsServiceByIdOrToken, RefreshCacheByRefreshToken refreshCacheByRefreshToken) {
        return new SecurityConfig(securityProperties, jwtUtils, userDetailsServiceByIdOrToken, refreshCacheByRefreshToken);
    }

    @Bean
    @ConditionalOnMissingBean(LoginService.class)
    public LoginService defaultLoginService(JwtUtils jwtUtils) {
        return new DefaultLoginService(jwtUtils);
    }

}
