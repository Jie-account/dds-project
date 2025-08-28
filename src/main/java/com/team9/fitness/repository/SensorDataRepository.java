package com.team9.fitness.repository;

import com.team9.fitness.entity.SensorData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 传感器数据仓库
 */
@Repository
public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    
    /**
     * 根据传感器类型查找最新的数据
     */
    Optional<SensorData> findFirstBySensorTypeOrderByCreatedAtDesc(SensorData.SensorType sensorType);
    
    /**
     * 根据传感器类型查找指定时间范围内的数据
     */
    List<SensorData> findBySensorTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            SensorData.SensorType sensorType, 
            LocalDateTime startTime, 
            LocalDateTime endTime);
    
    /**
     * 根据传感器类型查找最近N条数据
     */
    List<SensorData> findTop10BySensorTypeOrderByCreatedAtDesc(SensorData.SensorType sensorType);
    
    /**
     * 计算指定时间范围内的平均值
     */
    @Query("SELECT AVG(s.value) FROM SensorData s WHERE s.sensorType = :sensorType AND s.createdAt BETWEEN :startTime AND :endTime")
    Double calculateAverageByTypeAndTimeRange(
            @Param("sensorType") SensorData.SensorType sensorType,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 删除指定时间之前的数据
     */
    void deleteByCreatedAtBefore(LocalDateTime time);
}
