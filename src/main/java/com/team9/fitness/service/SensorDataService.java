package com.team9.fitness.service;

import com.team9.fitness.entity.SensorData;
import com.team9.fitness.repository.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 传感器数据服务
 */
@Service
@Transactional
public class SensorDataService {

    private static final Logger log = LoggerFactory.getLogger(SensorDataService.class);

    @Autowired
    private SensorDataRepository sensorDataRepository;

    /**
     * 保存传感器数据
     */
    public SensorData saveSensorData(SensorData sensorData) {
        log.info("保存传感器数据: 类型={}, 值={}", sensorData.getSensorType(), sensorData.getValue());
        return sensorDataRepository.save(sensorData);
    }

    /**
     * 获取最新的传感器数据
     */
    @Transactional(readOnly = true)
    public Optional<SensorData> getLatestSensorData(SensorData.SensorType sensorType) {
        return sensorDataRepository.findFirstBySensorTypeOrderByCreatedAtDesc(sensorType);
    }

    /**
     * 获取指定时间范围内的传感器数据
     */
    @Transactional(readOnly = true)
    public List<SensorData> getSensorDataByTimeRange(SensorData.SensorType sensorType,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return sensorDataRepository.findBySensorTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                sensorType, startTime, endTime);
    }

    /**
     * 获取最近10条传感器数据
     */
    @Transactional(readOnly = true)
    public List<SensorData> getRecentSensorData(SensorData.SensorType sensorType) {
        return sensorDataRepository.findTop10BySensorTypeOrderByCreatedAtDesc(sensorType);
    }

    /**
     * 计算指定时间范围内的平均值
     */
    @Transactional(readOnly = true)
    public Double calculateAverage(SensorData.SensorType sensorType,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return sensorDataRepository.calculateAverageByTypeAndTimeRange(sensorType, startTime, endTime);
    }

    /**
     * 清理旧数据
     */
    public void cleanupOldData(LocalDateTime beforeTime) {
        log.info("清理{}之前的数据", beforeTime);
        sensorDataRepository.deleteByCreatedAtBefore(beforeTime);
    }
}
