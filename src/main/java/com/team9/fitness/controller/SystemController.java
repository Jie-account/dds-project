package com.team9.fitness.controller;

import com.team9.fitness.entity.SystemConfig;
import com.team9.fitness.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

    private static final Logger log = LoggerFactory.getLogger(SystemController.class);

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 获取配置值
     */
    @GetMapping("/config/{configKey}")
    public ResponseEntity<String> getConfigValue(@PathVariable String configKey) {
        Optional<String> configValue = systemConfigService.getConfigValue(configKey);
        return configValue.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取配置对象
     */
    @GetMapping("/config/{configKey}/full")
    public ResponseEntity<SystemConfig> getConfig(@PathVariable String configKey) {
        Optional<SystemConfig> config = systemConfigService.getConfig(configKey);
        return config.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取数值配置
     */
    @GetMapping("/config/{configKey}/double")
    public ResponseEntity<Double> getDoubleConfig(@PathVariable String configKey) {
        Optional<Double> configValue = systemConfigService.getDoubleConfig(configKey);
        return configValue.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取整数配置
     */
    @GetMapping("/config/{configKey}/integer")
    public ResponseEntity<Integer> getIntegerConfig(@PathVariable String configKey) {
        Optional<Integer> configValue = systemConfigService.getIntegerConfig(configKey);
        return configValue.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 保存或更新配置
     */
    @PostMapping("/config")
    public ResponseEntity<SystemConfig> saveConfig(
            @RequestParam String configKey,
            @RequestParam String configValue,
            @RequestParam(required = false) String description) {

        SystemConfig config = systemConfigService.saveConfig(configKey, configValue, description);
        return ResponseEntity.ok(config);
    }

    /**
     * 删除配置
     */
    @DeleteMapping("/config/{configKey}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String configKey) {
        systemConfigService.deleteConfig(configKey);
        return ResponseEntity.ok().build();
    }

    /**
     * 检查配置是否存在
     */
    @GetMapping("/config/{configKey}/exists")
    public ResponseEntity<Boolean> configExists(@PathVariable String configKey) {
        boolean exists = systemConfigService.configExists(configKey);
        return ResponseEntity.ok(exists);
    }
}
