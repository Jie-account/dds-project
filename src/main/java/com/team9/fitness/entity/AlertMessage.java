package com.team9.fitness.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * 警报信息实体
 */
@Entity
@Table(name = "alert_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 警报级别
     */
    @Column(name = "level", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertLevel level;

    /**
     * 警报标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 警报内容
     */
    @Column(name = "content", nullable = false, length = 1000)
    private String content;

    /**
     * 传感器类型
     */
    @Column(name = "sensor_type")
    @Enumerated(EnumType.STRING)
    private SensorData.SensorType sensorType;

    /**
     * 触发值
     */
    @Column(name = "trigger_value")
    private Double triggerValue;

    /**
     * 阈值
     */
    @Column(name = "threshold")
    private Double threshold;

    /**
     * 是否已读
     */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 警报级别枚举
     */
    public enum AlertLevel {
        INFO("信息"),
        WARNING("警告"),
        ERROR("错误"),
        CRITICAL("严重");

        private final String description;

        AlertLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
    }
}
