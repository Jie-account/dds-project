package com.team9.fitness.service;

import com.team9.fitness.dto.SignInDTO;
import com.team9.fitness.dto.SignUpDTO;
import com.team9.fitness.entity.User;
import com.team9.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户登录
     */
    public ResponseEntity<Map<String, Object>> signIn(SignInDTO signInDTO) {
        Map<String, Object> response = new HashMap<>();

        // 参数验证
        String validationMessage = signInVerify(signInDTO);
        if (validationMessage != null) {
            response.put("success", false);
            response.put("message", validationMessage);
            return ResponseEntity.badRequest().body(response);
        }

        // 查询用户
        Long count = userRepository.judge(signInDTO.getUsername(), signInDTO.getPassword());

        if (count > 0) {
            // 获取用户信息
            User user = userRepository.findByUsername(signInDTO.getUsername());
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.ok(response); // 返回200状态码，让前端根据success字段判断
        }
    }

    /**
     * 用户注册
     */
    public ResponseEntity<Map<String, Object>> signUp(SignUpDTO signUpDTO) {
        Map<String, Object> response = new HashMap<>();

        // 参数验证
        String validationMessage = signUpVerify(signUpDTO);
        if (validationMessage != null) {
            response.put("success", false);
            response.put("message", validationMessage);
            return ResponseEntity.badRequest().body(response);
        }

        // 检查用户名是否已存在
        Long existingCount = userRepository.countByUsername(signUpDTO.getUsername());
        if (existingCount > 0) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.ok(response); // 返回200状态码，让前端根据success字段判断
        }

        // 创建新用户
        User newUser = new User();
        newUser.setUsername(signUpDTO.getUsername());
        newUser.setPassword(signUpDTO.getPassword());

        try {
            User savedUser = userRepository.save(newUser);
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("user", savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "注册失败：" + e.getMessage());
            return ResponseEntity.ok(response); // 返回200状态码，让前端根据success字段判断
        }
    }

    /**
     * 登录参数验证
     */
    private String signInVerify(SignInDTO signInDTO) {
        if (signInDTO == null) {
            return "登录信息不能为空";
        }
        if (!StringUtils.hasText(signInDTO.getUsername())) {
            return "用户名不能为空";
        }
        if (!StringUtils.hasText(signInDTO.getPassword())) {
            return "密码不能为空";
        }
        if (signInDTO.getUsername().length() > 20) {
            return "用户名长度不能超过20个字符";
        }
        if (signInDTO.getPassword().length() < 6 || signInDTO.getPassword().length() > 20) {
            return "密码长度必须在6到20个字符之间";
        }
        return null; // 验证通过返回null
    }

    /**
     * 注册参数验证
     */
    private String signUpVerify(SignUpDTO signUpDTO) {
        if (signUpDTO == null) {
            return "注册信息不能为空";
        }
        if (!StringUtils.hasText(signUpDTO.getUsername())) {
            return "用户名不能为空";
        }
        if (!StringUtils.hasText(signUpDTO.getPassword())) {
            return "密码不能为空";
        }
        if (!StringUtils.hasText(signUpDTO.getConfirmPassword())) {
            return "确认密码不能为空";
        }
        if (!signUpDTO.getPassword().equals(signUpDTO.getConfirmPassword())) {
            return "两次输入的密码不一致";
        }
        if (signUpDTO.getUsername().length() > 20) {
            return "用户名长度不能超过20个字符";
        }
        if (signUpDTO.getPassword().length() < 6 || signUpDTO.getPassword().length() > 20) {
            return "密码长度必须在6到20个字符之间";
        }
        return null; // 验证通过返回null
    }

    /**
     * 根据用户名查找用户
     */
    public ResponseEntity<Map<String, Object>> getUserByUsername(String username) {
        Map<String, Object> response = new HashMap<>();

        if (!StringUtils.hasText(username)) {
            response.put("success", false);
            response.put("message", "用户名不能为空");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userRepository.findByUsername(username);
        if (user != null) {
            response.put("success", true);
            response.put("user", user);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户不存在");
            return ResponseEntity.ok(response); // 返回200状态码，让前端根据success字段判断
        }
    }

}
