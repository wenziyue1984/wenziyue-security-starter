package com.wenziyue.security;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.wenziyue.security.entity.UserEntity;
import com.wenziyue.security.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author wenziyue
 */
@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        val userEntity = userMapper.selectOne(Wrappers.<UserEntity>lambdaQuery().eq(UserEntity::getName, username));
        if (userEntity == null) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        return new LoginUser(userEntity);
    }


}
