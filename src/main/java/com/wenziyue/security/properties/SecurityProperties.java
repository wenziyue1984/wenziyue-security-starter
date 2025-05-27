package com.wenziyue.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author wenziyue
 */
@Data
@ConfigurationProperties(prefix = "wenziyue.security")
//@Component
public class SecurityProperties {

    /**
     * 是否启用默认的 /login 接口
     */
    private Boolean defaultLoginEnabled = false;

    /**
     * JWT 的请求头名称
     */
    private String tokenHeader = "Authorization";

    /**
     * JWT 前缀（如："Bearer "）
     */
    private String tokenPrefix = "Bearer ";

    /**
     * 白名单路径（不需要登录认证的路径），支持用户自定义
     */
    private List<String> whiteList = new ArrayList<>();

    private Jwt jwt = new Jwt();

    @Data
    public static class Jwt {
        private String secret = "wenziyue-secret-wenziyue-secret-wenziyue-secret"; // 默认值
        private Long expiration = 604800000L;   // 默认 7 天（毫秒）
    }

    @PostConstruct
    public void init() {
        // 默认白名单路径
        List<String> defaultWhiteList = Arrays.asList(
                "/login",
                "/refresh",
                "/register",
                "/doc.html",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/webjars/swagger-ui/**"
        );
        // 合并默认路径（避免重复添加）
        for (String path : defaultWhiteList) {
            if (!whiteList.contains(path)) {
                whiteList.add(path);
            }
        }
    }
}