package com.wenziyue.security.service.impl;

import com.wenziyue.security.model.LoginRequest;
import com.wenziyue.security.service.LoginService;
import com.wenziyue.security.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;

/**
 * @author wenziyue
 */
//@Service
//@ConditionalOnMissingBean(LoginService.class)
@RequiredArgsConstructor
public class DefaultLoginService implements LoginService {

    private final JwtUtils jwtUtils;

    @Override
    public String login(LoginRequest request) {
        // 模拟用户名密码校验（实际情况应访问数据库）
        if ("admin".equals(request.getUsername()) && "123456".equals(request.getPassword())) {
            return jwtUtils.generateToken(request.getUsername());
        }
        throw new RuntimeException("用户名或密码错误");
    }
}
