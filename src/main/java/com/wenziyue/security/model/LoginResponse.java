package com.wenziyue.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author wenziyue
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
}
