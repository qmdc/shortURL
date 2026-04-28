package com.qiandao.training.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiandao.training.dao.UserDao;
import com.qiandao.training.entity.UserEntity;
import com.qiandao.training.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {
}
