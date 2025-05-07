package com.wenziyue.security.filter;

import com.wenziyue.security.service.UserDetailsServiceById;
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
    private final UserDetailsServiceById userDetailsServiceById;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, UserDetailsServiceById userDetailsServiceById) {
        this.jwtUtils = jwtUtils;
        this.userDetailsServiceById = userDetailsServiceById;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 从请求头中取出 Authorization: Bearer xxx
        String token = getTokenFromRequest(request);

        // 2. 校验 token 合法性
        if (StringUtils.hasText(token)) {
            try {
                // 3. 从 token 中获取用户信息
                String userId = jwtUtils.getUserIdFromToken(token);
                if (StringUtils.hasText(userId)) {
                    // 4. 构造认证对象
                    UserDetails userDetails = userDetailsServiceById.loadUserById(userId);
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // 5. 放入 Spring Security 上下文中
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // ==== 添加续期逻辑 ====
                    long remain = jwtUtils.getExpirationRemaining(token);
                    // 如果 token 剩余时间 < 一天，生成新 token 返回给前端
                    if (remain < 24 * 60 * 60 * 1000) {
                        String newToken = jwtUtils.generateToken(userId);
                        response.setHeader("X-Refresh-Token", newToken);
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
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7); // 去掉 "Bearer "
        }
        return null;
    }
}
