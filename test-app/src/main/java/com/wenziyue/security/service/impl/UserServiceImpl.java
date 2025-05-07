package com.wenziyue.security.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wenziyue.security.entity.UserEntity;
import com.wenziyue.security.mapper.UserMapper;
import com.wenziyue.security.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author wenziyue
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

}
