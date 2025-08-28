package com.team9.fitness.service;

import com.team9.fitness.entity.SystemConfig;
import com.team9.fitness.repository.SystemConfigRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 系统配置服务
 */
@Service
@Transactional
public class SystemConfigService {

    private static final Logger log = LoggerFactory.getLogger(SystemConfigService.class);

    @Autowired
    private SystemConfigRepository systemConfigRepository;

    /**
     * 保存或更新配置
     */
    public SystemConfig saveConfig(String configKey, String configValue, String description) {
        Optional<SystemConfig> existingConfig = systemConfigRepository.findByConfigKey(configKey);

        if (existingConfig.isPresent()) {
            SystemConfig config = existingConfig.get();
            config.setConfigValue(configValue);
            if (description != null) {
                config.setDescription(description);
            }
            log.info("更新配置: key={}, value={}", configKey, configValue);
            return systemConfigRepository.save(config);
        } else {
            SystemConfig newConfig = SystemConfig.builder()
                    .configKey(configKey)
                    .configValue(configValue)
                    .description(description)
                    .build();
            log.info("创建配置: key={}, value={}", configKey, configValue);
            return systemConfigRepository.save(newConfig);
        }
    }

    /**
     * 获取配置值
     */
    @Transactional(readOnly = true)
    public Optional<String> getConfigValue(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey)
                .map(SystemConfig::getConfigValue);
    }

    /**
     * 获取配置对象
     */
    @Transactional(readOnly = true)
    public Optional<SystemConfig> getConfig(String configKey) {
        return systemConfigRepository.findByConfigKey(configKey);
    }

    /**
     * 获取数值配置
     */
    @Transactional(readOnly = true)
    public Optional<Double> getDoubleConfig(String configKey) {
        return getConfigValue(configKey)
                .map(value -> {
                    try {
                        return Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        log.warn("配置值不是有效的数字: key={}, value={}", configKey, value);
                        return null;
                    }
                });
    }

    /**
     * 获取整数配置
     */
    @Transactional(readOnly = true)
    public Optional<Integer> getIntegerConfig(String configKey) {
        return getConfigValue(configKey)
                .map(value -> {
                    try {
                        return Integer.parseInt(value);
                    } catch (NumberFormatException e) {
                        log.warn("配置值不是有效的整数: key={}, value={}", configKey, value);
                        return null;
                    }
                });
    }

    /**
     * 检查配置是否存在
     */
    @Transactional(readOnly = true)
    public boolean configExists(String configKey) {
        return systemConfigRepository.existsByConfigKey(configKey);
    }

    /**
     * 删除配置
     */
    public void deleteConfig(String configKey) {
        systemConfigRepository.findByConfigKey(configKey)
                .ifPresent(config -> {
                    systemConfigRepository.delete(config);
                    log.info("删除配置: key={}", configKey);
                });
    }
}
