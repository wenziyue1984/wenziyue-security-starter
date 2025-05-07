package com.wenziyue.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义登录失败处理器
 * @author wenziyue
 */
@Component
public class MyLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String msg;

        if (exception instanceof UsernameNotFoundException) {
            msg = "用户名不存在";
        } else if (exception instanceof BadCredentialsException) {
            msg = "用户名或密码错误";
        } else if (exception instanceof LockedException) {
            msg = "账户被锁定";
        } else {
            msg = "登录失败：" + exception.getMessage();
        }

        response.getWriter().write("{\"error\": \"" + msg + "\"}");
    }
}
