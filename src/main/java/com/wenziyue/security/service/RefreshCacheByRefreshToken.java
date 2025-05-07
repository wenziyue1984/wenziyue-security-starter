package com.wenziyue.security.service;

/**
 * 刷新token时刷新缓存
 *
 * @author wenziyue
 */
public interface RefreshCacheByRefreshToken {

    void refreshCacheByRefreshToken(String oldToken);
}
