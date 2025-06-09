package com.wenziyue.security.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wenziyue
 */
public interface UserDetailsServiceByIdOrToken {

    /**
     * 通过userId或token查询用户信息和权限信息
     */
    UserDetails loadUserByUserIdOrToken(String id, String token);
}
