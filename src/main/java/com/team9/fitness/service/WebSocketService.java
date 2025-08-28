package com.team9.fitness.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * WebSocket服务
 * 用于向客户端推送实时数据
 */
@Service
public class WebSocketService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketService.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 推送传感器数据到指定主题
     */
    public void pushSensorData(String sensorType, Object data) {
        try {
            String topic = "/topic/sensor/" + sensorType;
            messagingTemplate.convertAndSend(topic, data);
            log.debug("推送传感器数据到主题: {}, 数据: {}", topic, data);
        } catch (Exception e) {
            log.error("推送传感器数据失败: sensorType={}, data={}", sensorType, data, e);
        }
    }

    /**
     * 推送温度数据
     */
    public void pushTemperatureData(Object data) {
        pushSensorData("temperature", data);
    }

    /**
     * 推送湿度数据
     */
    public void pushHumidityData(Object data) {
        pushSensorData("humidity", data);
    }

    /**
     * 推送警报信息
     */
    public void pushAlertData(Object data) {
        try {
            String topic = "/topic/alerts";
            messagingTemplate.convertAndSend(topic, data);
            log.debug("推送警报数据到主题: {}, 数据: {}", topic, data);
        } catch (Exception e) {
            log.error("推送警报数据失败: data={}", data, e);
        }
    }

    /**
     * 推送系统状态信息
     */
    public void pushSystemStatus(Object data) {
        try {
            String topic = "/topic/system/status";
            messagingTemplate.convertAndSend(topic, data);
            log.debug("推送系统状态到主题: {}, 数据: {}", topic, data);
        } catch (Exception e) {
            log.error("推送系统状态失败: data={}", data, e);
        }
    }

    /**
     * 向特定用户推送消息
     */
    public void pushToUser(String username, String destination, Object data) {
        try {
            String userTopic = "/user/" + username + destination;
            messagingTemplate.convertAndSendToUser(username, destination, data);
            log.debug("向用户推送消息: user={}, destination={}, data={}", username, destination, data);
        } catch (Exception e) {
            log.error("向用户推送消息失败: username={}, destination={}, data={}", username, destination, data, e);
        }
    }
}
