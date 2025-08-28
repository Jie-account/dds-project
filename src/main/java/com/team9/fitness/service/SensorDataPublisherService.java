package com.team9.fitness.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.fitness.entity.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * 传感器数据发布服务
 * 基于ZRDDS实现真实的传感器数据发布
 */
@Service
public class SensorDataPublisherService {

    private static final Logger log = LoggerFactory.getLogger(SensorDataPublisherService.class);

    @Autowired
    private ZRDDSService zrddsService;

    @Autowired
    private SensorDataService sensorDataService;

    @Value("${sensor.temperature.interval:5000}")
    private long temperatureInterval;

    @Value("${sensor.humidity.interval:8000}")
    private long humidityInterval;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    private final Random random = new Random();

    /**
     * 发布温度传感器数据
     */
    @Scheduled(fixedDelayString = "${sensor.temperature.interval:5000}")
    public void publishTemperatureData() {
        if (!zrddsService.isInitialized()) {
            log.warn("ZRDDS服务未初始化，跳过温度数据发布");
            return;
        }

        try {
            // 生成模拟温度数据
            double temperature = generateTemperatureValue();

            // 创建传感器数据对象
            SensorData sensorData = SensorData.builder()
                    .sensorType(SensorData.SensorType.TEMPERATURE)
                    .value(temperature)
                    .unit("°C")
                    .createdAt(LocalDateTime.now())
                    .build();

            // 保存到数据库
            sensorDataService.saveSensorData(sensorData);

            // 序列化为JSON
            String jsonData = objectMapper.writeValueAsString(sensorData);

            // 发布到ZRDDS主题
            zrddsService.publishData("Sensor/Temperature", jsonData);

            log.debug("温度数据发布成功: value={}°C", temperature);

        } catch (Exception e) {
            log.error("发布温度数据失败", e);
        }
    }

    /**
     * 发布湿度传感器数据
     */
    @Scheduled(fixedDelayString = "${sensor.humidity.interval:8000}")
    public void publishHumidityData() {
        if (!zrddsService.isInitialized()) {
            log.warn("ZRDDS服务未初始化，跳过湿度数据发布");
            return;
        }

        try {
            // 生成模拟湿度数据
            double humidity = generateHumidityValue();

            // 创建传感器数据对象
            SensorData sensorData = SensorData.builder()
                    .sensorType(SensorData.SensorType.HUMIDITY)
                    .value(humidity)
                    .unit("%")
                    .createdAt(LocalDateTime.now())
                    .build();

            // 保存到数据库
            sensorDataService.saveSensorData(sensorData);

            // 序列化为JSON
            String jsonData = objectMapper.writeValueAsString(sensorData);

            // 发布到ZRDDS主题
            zrddsService.publishData("Sensor/Humidity", jsonData);

            log.debug("湿度数据发布成功: value={}%", humidity);

        } catch (Exception e) {
            log.error("发布湿度数据失败", e);
        }
    }

    /**
     * 手动发布温度数据
     */
    public void publishManualTemperatureData(double temperature) {
        if (!zrddsService.isInitialized()) {
            throw new IllegalStateException("ZRDDS服务未初始化");
        }

        try {
            // 创建传感器数据对象
            SensorData sensorData = SensorData.builder()
                    .sensorType(SensorData.SensorType.TEMPERATURE)
                    .value(temperature)
                    .unit("°C")
                    .createdAt(LocalDateTime.now())
                    .build();

            // 保存到数据库
            sensorDataService.saveSensorData(sensorData);

            // 序列化为JSON
            String jsonData = objectMapper.writeValueAsString(sensorData);

            // 发布到ZRDDS主题
            zrddsService.publishData("Sensor/Temperature", jsonData);

            log.info("手动温度数据发布成功: value={}°C", temperature);

        } catch (Exception e) {
            log.error("手动发布温度数据失败", e);
            throw new RuntimeException("手动发布温度数据失败", e);
        }
    }

    /**
     * 手动发布湿度数据
     */
    public void publishManualHumidityData(double humidity) {
        if (!zrddsService.isInitialized()) {
            throw new IllegalStateException("ZRDDS服务未初始化");
        }

        try {
            // 创建传感器数据对象
            SensorData sensorData = SensorData.builder()
                    .sensorType(SensorData.SensorType.HUMIDITY)
                    .value(humidity)
                    .unit("%")
                    .createdAt(LocalDateTime.now())
                    .build();

            // 保存到数据库
            sensorDataService.saveSensorData(sensorData);

            // 序列化为JSON
            String jsonData = objectMapper.writeValueAsString(sensorData);

            // 发布到ZRDDS主题
            zrddsService.publishData("Sensor/Humidity", jsonData);

            log.info("手动湿度数据发布成功: value={}%", humidity);

        } catch (Exception e) {
            log.error("手动发布湿度数据失败", e);
            throw new RuntimeException("手动发布湿度数据失败", e);
        }
    }

    /**
     * 发布控制命令
     */
    public void publishControlCommand(String command) {
        if (!zrddsService.isInitialized()) {
            throw new IllegalStateException("ZRDDS服务未初始化");
        }

        try {
            zrddsService.publishData("Control/Command", command);
            log.info("控制命令发布成功: command={}", command);
        } catch (Exception e) {
            log.error("发布控制命令失败", e);
            throw new RuntimeException("发布控制命令失败", e);
        }
    }

    /**
     * 发布警报消息
     */
    public void publishAlertMessage(String alertMessage) {
        if (!zrddsService.isInitialized()) {
            throw new IllegalStateException("ZRDDS服务未初始化");
        }

        try {
            zrddsService.publishData("Alert/Message", alertMessage);
            log.info("警报消息发布成功: message={}", alertMessage);
        } catch (Exception e) {
            log.error("发布警报消息失败", e);
            throw new RuntimeException("发布警报消息失败", e);
        }
    }

    /**
     * 生成模拟温度值
     */
    private double generateTemperatureValue() {
        // 生成20-35度之间的随机温度
        return 20.0 + random.nextDouble() * 15.0;
    }

    /**
     * 生成模拟湿度值
     */
    private double generateHumidityValue() {
        // 生成40-90%之间的随机湿度
        return 40.0 + random.nextDouble() * 50.0;
    }
}
