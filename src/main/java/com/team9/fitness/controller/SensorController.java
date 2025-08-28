package com.team9.fitness.controller;

import com.team9.fitness.entity.SensorData;
import com.team9.fitness.service.SensorDataService;
import com.team9.fitness.service.SensorDataPublisherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 传感器数据控制器
 */
@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private static final Logger log = LoggerFactory.getLogger(SensorController.class);

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private SensorDataPublisherService sensorDataPublisherService;

    /**
     * 获取最新的传感器数据
     */
    @GetMapping("/latest/{sensorType}")
    public ResponseEntity<SensorData> getLatestSensorData(@PathVariable SensorData.SensorType sensorType) {
        Optional<SensorData> sensorData = sensorDataService.getLatestSensorData(sensorType);
        return sensorData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 获取指定时间范围内的传感器数据
     */
    @GetMapping("/{sensorType}/range")
    public ResponseEntity<List<SensorData>> getSensorDataByTimeRange(
            @PathVariable SensorData.SensorType sensorType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        List<SensorData> sensorDataList = sensorDataService.getSensorDataByTimeRange(sensorType, startTime, endTime);
        return ResponseEntity.ok(sensorDataList);
    }

    /**
     * 获取最近10条传感器数据
     */
    @GetMapping("/{sensorType}/recent")
    public ResponseEntity<List<SensorData>> getRecentSensorData(@PathVariable SensorData.SensorType sensorType) {
        List<SensorData> sensorDataList = sensorDataService.getRecentSensorData(sensorType);
        return ResponseEntity.ok(sensorDataList);
    }

    /**
     * 计算指定时间范围内的平均值
     */
    @GetMapping("/{sensorType}/average")
    public ResponseEntity<Double> calculateAverage(
            @PathVariable SensorData.SensorType sensorType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        Double average = sensorDataService.calculateAverage(sensorType, startTime, endTime);
        return ResponseEntity.ok(average != null ? average : 0.0);
    }

    /**
     * 手动生成温度数据
     */
    @PostMapping("/temperature/generate")
    public ResponseEntity<SensorData> generateTemperatureData() {
        double temperature = 20.0 + Math.random() * 15.0; // 生成20-35度之间的随机温度
        sensorDataPublisherService.publishManualTemperatureData(temperature);
        
        // 获取最新保存的数据
        Optional<SensorData> sensorData = sensorDataService.getLatestSensorData(SensorData.SensorType.TEMPERATURE);
        return sensorData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok().build());
    }

    /**
     * 手动生成湿度数据
     */
    @PostMapping("/humidity/generate")
    public ResponseEntity<SensorData> generateHumidityData() {
        double humidity = 40.0 + Math.random() * 50.0; // 生成40-90%之间的随机湿度
        sensorDataPublisherService.publishManualHumidityData(humidity);
        
        // 获取最新保存的数据
        Optional<SensorData> sensorData = sensorDataService.getLatestSensorData(SensorData.SensorType.HUMIDITY);
        return sensorData.map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok().build());
    }

    /**
     * 检查发布服务状态
     */
    @GetMapping("/publisher/status")
    public ResponseEntity<Boolean> getPublisherStatus() {
        return ResponseEntity.ok(true); // 发布服务总是可用的
    }
}
