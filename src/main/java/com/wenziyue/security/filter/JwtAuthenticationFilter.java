package com.wenziyue.security.filter;

import com.wenziyue.security.properties.SecurityProperties;
import com.wenziyue.security.service.RefreshCacheByRefreshToken;
import com.wenziyue.security.service.UserDetailsServiceByIdOrToken;
import com.wenziyue.security.utils.JwtUtils;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author wenziyue
 */
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceByIdOrToken userDetailsServiceByIdOrToken;
    private final SecurityProperties securityProperties;
    private final RefreshCacheByRefreshToken refreshCacheByRefreshToken;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceByIdOrToken userDetailsServiceByIdOrToken,
                                   SecurityProperties securityProperties, RefreshCacheByRefreshToken refreshCacheByRefreshToken) {
        this.jwtUtils = jwtUtils;
        this.userDetailsServiceByIdOrToken = userDetailsServiceByIdOrToken;
        this.securityProperties = securityProperties;
        this.refreshCacheByRefreshToken = refreshCacheByRefreshToken;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头中取出 Authorization: Bearer xxx
        String oldToken = getTokenFromRequest(request);

        // 2. 校验 oldToken 合法性
        if (StringUtils.hasText(oldToken)) {
            try {
                // 3. 从 oldToken 中获取用户信息
                String userId = jwtUtils.getUserIdFromToken(oldToken); // 这里会校验jwt是否过期
                if (StringUtils.hasText(userId)) {
                    // 4. 构造认证对象
                    UserDetails userDetails = userDetailsServiceByIdOrToken.loadUserByUserIdOrToken(userId, oldToken);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 5. 放入 Spring Security 上下文中
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // ==== 添加续期逻辑 ====
                    long remain = jwtUtils.getExpirationRemaining(oldToken);
                    // 如果 oldToken 剩余时间不足，生成新 oldToken 返回给前端
                    if (remain < securityProperties.getRefreshBeforeExpiration()) {
                        String newToken = jwtUtils.generateToken(userId);
                        response.setHeader(securityProperties.getRefreshTokenHeader(), newToken);
                        // 刷新token的时候也更新缓存
                        refreshCacheByRefreshToken.refreshCacheByRefreshToken(oldToken, newToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                log.warn("Token 已过期: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            } catch (JwtException e) {
                log.warn("Token 非法: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // 6. 放行
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearer = request.getHeader(securityProperties.getTokenHeader());
        if (StringUtils.hasText(bearer) && bearer.startsWith(securityProperties.getTokenPrefix())) {
            return bearer.substring(securityProperties.getTokenPrefix().length() + 1); // 去掉 "Bearer ",+1是给空格的长度
        }
        return null;
    }
}
