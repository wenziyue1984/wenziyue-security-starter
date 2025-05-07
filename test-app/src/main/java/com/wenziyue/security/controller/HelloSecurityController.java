package com.wenziyue.security.controller;

import com.wenziyue.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.wenziyue.security.LoginUser;
import com.wenziyue.security.LoginRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;


/**
 * @author wenziyue
 */

@RestController
@RequiredArgsConstructor
public class HelloSecurityController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring Security!";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "this is admin dashboard.";
    }

    @GetMapping("/public")
    public String publicTest() {
        return "publicTest";
    }

    @GetMapping("/user/info")
    @PreAuthorize("hasRole('USER')")
    public String userInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoginUser user = (LoginUser) auth.getPrincipal();
        return "你好，" + user.getUsername();
    }

    @GetMapping("/admin/info")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoginUser user = (LoginUser) auth.getPrincipal();
        return "你好，管理员：" + user.getUsername();
    }

}
