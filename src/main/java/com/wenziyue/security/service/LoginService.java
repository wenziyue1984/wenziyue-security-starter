package com.wenziyue.security.service;

import com.wenziyue.security.model.LoginRequest;

/**
 * @author wenziyue
 */
public interface LoginService {

    String login(LoginRequest request);
}
