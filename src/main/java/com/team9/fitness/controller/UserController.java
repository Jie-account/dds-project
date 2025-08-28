package com.team9.fitness.controller;

import com.team9.fitness.dto.SignInDTO;
import com.team9.fitness.dto.SignUpDTO;
import com.team9.fitness.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/signIn")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody SignInDTO signInDTO) {
        log.info("收到登录请求: username={}", signInDTO != null ? signInDTO.getUsername() : "null");
        try {
            return userService.signIn(signInDTO);
        } catch (Exception e) {
            log.error("登录处理异常", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "登录处理失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/signUp")
    public ResponseEntity<Map<String, Object>> signUp(@RequestBody SignUpDTO signUpDTO) {
        log.info("收到注册请求: username={}", signUpDTO != null ? signUpDTO.getUsername() : "null");
        try {
            return userService.signUp(signUpDTO);
        } catch (Exception e) {
            log.error("注册处理异常", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "注册处理失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

    /**
     * 根据用户名查找用户
     */
    @GetMapping("/{username}")
    public ResponseEntity<Map<String, Object>> getUserByUsername(@PathVariable String username) {
        log.info("查询用户: username={}", username);
        return userService.getUserByUsername(username);
    }

    /**
     * 检查用户名是否已存在
     */
    @GetMapping("/check-username/{username}")
    public ResponseEntity<Map<String, Object>> checkUsernameExists(@PathVariable String username) {
        log.info("检查用户名是否存在: username={}", username);
        return userService.getUserByUsername(username);
    }

}
