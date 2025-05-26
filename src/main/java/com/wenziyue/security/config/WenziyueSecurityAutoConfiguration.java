package com.wenziyue.security.config;

import com.wenziyue.security.controller.LoginController;
import com.wenziyue.security.properties.SecurityProperties;
import com.wenziyue.security.service.LoginService;
import com.wenziyue.security.service.RefreshCacheByRefreshToken;
import com.wenziyue.security.service.UserDetailsServiceById;
import com.wenziyue.security.service.impl.DefaultLoginService;
import com.wenziyue.security.utils.JwtUtils;
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
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
@EnableWebSecurity
//@ComponentScan(basePackages = "com.wenziyue.security")
public class WenziyueSecurityAutoConfiguration {

    @Bean
    public JwtUtils jwtUtils(SecurityProperties properties) {
        return new JwtUtils(properties);
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsServiceById defaultUserDetailsServiceById() {
        return userId -> {
            // 默认实现只返回 userId，无权限信息
            return new org.springframework.security.core.userdetails.User(
                    String.valueOf(userId),
                    "",
                    Collections.emptyList()
            );
        };
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
            SecurityProperties securityProperties,
            JwtUtils jwtUtils,
            UserDetailsServiceById userDetailsServiceById) {
        return new SecurityConfig(securityProperties, jwtUtils, userDetailsServiceById);
    }

    @Bean
    @ConditionalOnMissingBean(LoginService.class)
    public LoginService defaultLoginService(JwtUtils jwtUtils) {
        return new DefaultLoginService(jwtUtils);
    }

}
