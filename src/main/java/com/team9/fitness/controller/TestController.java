package com.team9.fitness.controller;

import com.team9.fitness.entity.Test;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @PostMapping("/test")
    public String test(@RequestBody Test test) {
        return "test success";
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "API服务正常运行");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "请求接收成功");
        response.put("data", request);
        return response;
    }
}
