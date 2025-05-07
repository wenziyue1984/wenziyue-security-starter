package com.wenziyue.security.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author wenziyue
 */
public interface UserDetailsServiceById {

    /**
     * 通过 userId 查询用户信息和权限信息
     */
    UserDetails loadUserById(String userId);
}
