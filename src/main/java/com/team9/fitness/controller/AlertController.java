package com.team9.fitness.controller;

import com.team9.fitness.entity.AlertMessage;
import com.team9.fitness.service.AlertMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 警报信息控制器
 */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private static final Logger log = LoggerFactory.getLogger(AlertController.class);

    @Autowired
    private AlertMessageService alertMessageService;

    /**
     * 获取未读警报列表
     */
    @GetMapping("/unread")
    public ResponseEntity<List<AlertMessage>> getUnreadAlerts() {
        List<AlertMessage> alerts = alertMessageService.getUnreadAlerts();
        return ResponseEntity.ok(alerts);
    }

    /**
     * 获取指定时间范围内的警报
     */
    @GetMapping("/range")
    public ResponseEntity<List<AlertMessage>> getAlertsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {

        List<AlertMessage> alerts = alertMessageService.getAlertsByTimeRange(startTime, endTime);
        return ResponseEntity.ok(alerts);
    }

    /**
     * 根据级别获取警报
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<List<AlertMessage>> getAlertsByLevel(@PathVariable AlertMessage.AlertLevel level) {
        List<AlertMessage> alerts = alertMessageService.getAlertsByLevel(level);
        return ResponseEntity.ok(alerts);
    }

    /**
     * 标记警报为已读
     */
    @PutMapping("/{alertId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long alertId) {
        alertMessageService.markAsRead(alertId);
        return ResponseEntity.ok().build();
    }

    /**
     * 标记所有警报为已读
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        alertMessageService.markAllAsRead();
        return ResponseEntity.ok().build();
    }

    /**
     * 获取未读警报数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadAlertCount() {
        long count = alertMessageService.getUnreadAlertCount();
        return ResponseEntity.ok(count);
    }

    /**
     * 创建自定义警报
     */
    @PostMapping
    public ResponseEntity<AlertMessage> createAlert(@RequestBody AlertMessage alertMessage) {
        AlertMessage createdAlert = alertMessageService.createAlert(alertMessage);
        return ResponseEntity.ok(createdAlert);
    }
}
