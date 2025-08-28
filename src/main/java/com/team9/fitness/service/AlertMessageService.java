package com.team9.fitness.service;

import com.team9.fitness.entity.AlertMessage;
import com.team9.fitness.entity.SensorData;
import com.team9.fitness.repository.AlertMessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 警报信息服务
 */
@Service
@Transactional
public class AlertMessageService {

    private static final Logger log = LoggerFactory.getLogger(AlertMessageService.class);

    @Autowired
    private AlertMessageRepository alertMessageRepository;

    /**
     * 创建警报信息
     */
    public AlertMessage createAlert(AlertMessage alertMessage) {
        log.info("创建警报: 级别={}, 标题={}", alertMessage.getLevel(), alertMessage.getTitle());
        return alertMessageRepository.save(alertMessage);
    }

    /**
     * 根据传感器数据创建警报
     */
    public AlertMessage createSensorAlert(SensorData sensorData, double threshold, AlertMessage.AlertLevel level) {
        String title = String.format("%s传感器警报", sensorData.getSensorType().getDescription());
        String content = String.format("%s传感器数值%.2f%s超过阈值%.2f%s",
                sensorData.getSensorType().getDescription(),
                sensorData.getValue(),
                sensorData.getUnit(),
                threshold,
                sensorData.getUnit());

        AlertMessage alert = AlertMessage.builder()
                .level(level)
                .title(title)
                .content(content)
                .sensorType(sensorData.getSensorType())
                .triggerValue(sensorData.getValue())
                .threshold(threshold)
                .isRead(false)
                .build();

        return createAlert(alert);
    }

    /**
     * 获取未读警报
     */
    @Transactional(readOnly = true)
    public List<AlertMessage> getUnreadAlerts() {
        return alertMessageRepository.findByIsReadFalseOrderByCreatedAtDesc();
    }

    /**
     * 获取指定时间范围内的警报
     */
    @Transactional(readOnly = true)
    public List<AlertMessage> getAlertsByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return alertMessageRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(startTime, endTime);
    }

    /**
     * 根据级别获取警报
     */
    @Transactional(readOnly = true)
    public List<AlertMessage> getAlertsByLevel(AlertMessage.AlertLevel level) {
        return alertMessageRepository.findByLevelOrderByCreatedAtDesc(level);
    }

    /**
     * 标记警报为已读
     */
    public void markAsRead(Long alertId) {
        alertMessageRepository.markAsRead(alertId);
        log.info("标记警报为已读: ID={}", alertId);
    }

    /**
     * 标记所有警报为已读
     */
    public void markAllAsRead() {
        alertMessageRepository.markAllAsRead();
        log.info("标记所有警报为已读");
    }

    /**
     * 获取未读警报数量
     */
    @Transactional(readOnly = true)
    public long getUnreadAlertCount() {
        return alertMessageRepository.countByIsReadFalse();
    }

    /**
     * 清理旧警报
     */
    public void cleanupOldAlerts(LocalDateTime beforeTime) {
        log.info("清理{}之前的警报", beforeTime);
        alertMessageRepository.deleteByCreatedAtBefore(beforeTime);
    }
}
