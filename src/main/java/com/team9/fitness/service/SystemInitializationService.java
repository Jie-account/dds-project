package com.team9.fitness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

/**
 * 系统初始化服务
 * 负责系统启动时的初始化工作
 */
@Service
public class SystemInitializationService implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemInitializationService.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private DataListenerService dataListenerService;

    @Value("${sensor.temperature.threshold:30.0}")
    private double defaultTemperatureThreshold;

    @Value("${sensor.humidity.threshold:80.0}")
    private double defaultHumidityThreshold;

    @Override
    public void run(String... args) throws Exception {
        log.info("开始系统初始化...");

        try {
            // 初始化系统配置
            initializeSystemConfigs();

            // 初始化数据监听器
            dataListenerService.initialize();

            log.info("系统初始化完成");

        } catch (Exception e) {
            log.error("系统初始化失败", e);
            throw e;
        }
    }

    /**
     * 初始化系统配置
     */
    private void initializeSystemConfigs() {
        log.info("初始化系统配置...");

        // 初始化温度阈值配置
        if (!systemConfigService.configExists("sensor.temperature.threshold")) {
            systemConfigService.saveConfig(
                    "sensor.temperature.threshold",
                    String.valueOf(defaultTemperatureThreshold),
                    "温度传感器阈值配置");
            log.info("创建温度阈值配置: {}", defaultTemperatureThreshold);
        }

        // 初始化湿度阈值配置
        if (!systemConfigService.configExists("sensor.humidity.threshold")) {
            systemConfigService.saveConfig(
                    "sensor.humidity.threshold",
                    String.valueOf(defaultHumidityThreshold),
                    "湿度传感器阈值配置");
            log.info("创建湿度阈值配置: {}", defaultHumidityThreshold);
        }

        // 初始化其他系统配置
        initializeOtherConfigs();

        log.info("系统配置初始化完成");
    }

    /**
     * 初始化其他系统配置
     */
    private void initializeOtherConfigs() {
        // 数据清理配置
        if (!systemConfigService.configExists("system.data.retention.days")) {
            systemConfigService.saveConfig(
                    "system.data.retention.days",
                    "30",
                    "数据保留天数配置");
        }

        // 警报级别配置
        if (!systemConfigService.configExists("system.alert.level.default")) {
            systemConfigService.saveConfig(
                    "system.alert.level.default",
                    "WARNING",
                    "默认警报级别配置");
        }

        // 系统名称配置
        if (!systemConfigService.configExists("system.name")) {
            systemConfigService.saveConfig(
                    "system.name",
                    "Fitness Sensor Monitoring System",
                    "系统名称配置");
        }

        // 系统版本配置
        if (!systemConfigService.configExists("system.version")) {
            systemConfigService.saveConfig(
                    "system.version",
                    "1.0.0",
                    "系统版本配置");
        }
    }
}
