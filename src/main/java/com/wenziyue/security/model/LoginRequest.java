package com.wenziyue.security.model;

import lombok.Data;

/**
 * @author wenziyue
 */
@Data
public class LoginRequest {

    private String username;
    private String password;
}
