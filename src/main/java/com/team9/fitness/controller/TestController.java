package com.team9.fitness.controller;

import com.team9.fitness.entity.Test;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class TestController {

    @PostMapping("/test")
    public String test(@RequestBody Test test) {
            return "test success";
        }
}
