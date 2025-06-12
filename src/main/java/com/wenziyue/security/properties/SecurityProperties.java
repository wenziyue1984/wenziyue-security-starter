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
     * 刷新token时返回的header头
     */
    private String refreshTokenHeader = "X-Refresh-Token";

    /**
     * JWT 前缀（如："Bearer "）
     */
    private String tokenPrefix = "Bearer";

    /**
     * token的过期时间（毫秒），默认7天
     */
    private Long expire = 7 * 24 * 60 * 60L;

    /**
     * 还剩多少时间开始刷新token（毫秒）， 默认1天
     */
    private Long refreshBeforeExpiration = 24 * 60 * 60L;

    /**
     * JWT 密钥
     */
    private String jwtSecret = "wenziyue-secret-wenziyue-secret-wenziyue-secret";

    /**
     * 白名单路径（不需要登录认证的路径），支持用户自定义
     */
    private List<String> whiteList = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 默认token过期时间，7天（毫秒）
        if (expire == null) {
            expire = 7 * 24 * 60 * 60L;
        }
        // 默认刷新token时间，1天（毫秒）
        if (refreshBeforeExpiration == null) {
            refreshBeforeExpiration = 24 * 60 * 60L;
        }
        // 校验参数
        validate();
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

    /**
     * 校验参数
     */
    private void validate() {
        if (expire <= 0) {
            throw new IllegalArgumentException("expire must be greater than 0");
        }
        if (refreshBeforeExpiration <= 0) {
            throw new IllegalArgumentException("refreshBeforeExpiration must be greater than 0");
        }
        if (expire <= refreshBeforeExpiration) {
            throw new IllegalArgumentException("expire must be greater than refreshBeforeExpiration");
        }
    }
}