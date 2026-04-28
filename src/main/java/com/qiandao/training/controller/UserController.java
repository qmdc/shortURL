package com.qiandao.training.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qiandao.training.entity.UserEntity;
import com.qiandao.training.global.R;
import com.qiandao.training.service.UserService;
import com.qiandao.training.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public R register(@RequestBody UserEntity user) {
        log.info("用户注册请求: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return R.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return R.error("密码不能为空");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            return R.error("邮箱不能为空");
        }

        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        UserEntity existUser = userService.getOne(wrapper);
        if (existUser != null) {
            return R.error("用户名已存在");
        }

        QueryWrapper<UserEntity> emailWrapper = new QueryWrapper<>();
        emailWrapper.eq("email", user.getEmail());
        UserEntity existEmail = userService.getOne(emailWrapper);
        if (existEmail != null) {
            return R.error("邮箱已被注册");
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        UserEntity newUser = UserEntity.builder()
                .username(user.getUsername())
                .password(encodedPassword)
                .email(user.getEmail())
                .build();

        boolean save = userService.save(newUser);
        if (save) {
            log.info("用户注册成功: {}", user.getUsername());
            return R.ok("注册成功");
        }
        return R.error("注册失败");
    }

    @PostMapping("/login")
    public R login(@RequestBody UserEntity user) {
        log.info("用户登录请求: {}", user.getUsername());

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            return R.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            return R.error("密码不能为空");
        }

        QueryWrapper<UserEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("username", user.getUsername());
        UserEntity existUser = userService.getOne(wrapper);

        if (existUser == null) {
            return R.error("用户不存在");
        }

        if (!passwordEncoder.matches(user.getPassword(), existUser.getPassword())) {
            return R.error("密码错误");
        }

        String token = jwtUtil.generateToken(existUser.getId(), existUser.getUsername());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", existUser.getId());
        result.put("username", existUser.getUsername());
        result.put("email", existUser.getEmail());

        log.info("用户登录成功: {}", user.getUsername());
        return R.ok().setData(result);
    }

    @GetMapping("/info")
    public R getUserInfo(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        if (!jwtUtil.validateToken(token)) {
            return R.error("token无效或已过期");
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        if (userId == null) {
            return R.error("无法获取用户信息");
        }

        UserEntity user = userService.getById(userId);
        if (user == null) {
            return R.error("用户不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("createTime", user.getCreate_time());

        return R.ok().setData(result);
    }
}
