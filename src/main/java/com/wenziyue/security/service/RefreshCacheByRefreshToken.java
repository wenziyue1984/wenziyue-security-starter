package com.wenziyue.security.service;

/**
 * 刷新token时刷新缓存
 *
 * @author wenziyue
 */
public interface RefreshCacheByRefreshToken {

    void refreshCacheByRefreshToken(String oldToken);

    /**
     * 判断缓存中是否由此token，无须判断的话直接返回true即可
     *
     * @param oldToken 刷新的旧token
     * @return 缓存中存在返回true
     */
    boolean isOldTokenExist(String oldToken);
}
