package com.wenziyue.security.config;

import com.wenziyue.security.service.RefreshCacheByRefreshToken;
import com.wenziyue.security.service.UserDetailsServiceByIdOrToken;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.http.SessionCreationPolicy;

import com.wenziyue.security.properties.SecurityProperties;
import com.wenziyue.security.utils.JwtUtils;
import com.wenziyue.security.filter.JwtAuthenticationFilter;

/**
 * @author wenziyue
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final JwtUtils jwtUtils;
    private final UserDetailsServiceByIdOrToken userDetailsServiceByIdOrToken;
    private final RefreshCacheByRefreshToken refreshCacheByRefreshToken;

    public SecurityConfig(SecurityProperties securityProperties, JwtUtils jwtUtils, UserDetailsServiceByIdOrToken userDetailsServiceByIdOrToken, RefreshCacheByRefreshToken refreshCacheByRefreshToken) {
        this.securityProperties = securityProperties;
        this.jwtUtils = jwtUtils;
        this.userDetailsServiceByIdOrToken = userDetailsServiceByIdOrToken;
        this.refreshCacheByRefreshToken = refreshCacheByRefreshToken;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, userDetailsServiceByIdOrToken, securityProperties, refreshCacheByRefreshToken);

        http
                .csrf().disable() // 禁用 csrf
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 无状态
                .and()
                .authorizeHttpRequests(authorize -> {
                    // 白名单放行
                    for (String path : securityProperties.getWhiteList()) {
                        authorize.antMatchers(path).permitAll();
                    }
                    authorize.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}
