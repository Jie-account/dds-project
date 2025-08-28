package com.team9.fitness.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.fitness.entity.AlertMessage;
import com.team9.fitness.entity.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 数据监听器服务
 * 处理ZRDDS主题订阅和数据监听
 */
@Service
public class DataListenerService implements ZRDDSService.DataListener {

    private static final Logger log = LoggerFactory.getLogger(DataListenerService.class);

    @Autowired
    private AlertMessageService alertMessageService;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private ZRDDSService zrddsService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    // 存储最近的数据用于计算平均值
    private final ConcurrentHashMap<String, AtomicInteger> dataCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> dataSums = new ConcurrentHashMap<>();

    // 阈值配置键
    private static final String TEMPERATURE_THRESHOLD_KEY = "sensor.temperature.threshold";
    private static final String HUMIDITY_THRESHOLD_KEY = "sensor.humidity.threshold";

    /**
     * 初始化数据监听器
     */
    public void initialize() {
        log.info("初始化数据监听器...");

        // 等待ZRDDS服务初始化完成
        if (!zrddsService.isInitialized()) {
            log.warn("ZRDDS服务未初始化，等待初始化完成...");
            return;
        }

        // 订阅传感器主题
        try {
            zrddsService.subscribeTopic("Sensor/Temperature", this);
            zrddsService.subscribeTopic("Sensor/Humidity", this);
            zrddsService.subscribeTopic("Control/Command", this);
            zrddsService.subscribeTopic("Alert/Message", this);

            log.info("数据监听器初始化完成，已订阅主题: {}", zrddsService.getSubscribedTopics());
        } catch (Exception e) {
            log.error("订阅ZRDDS主题失败", e);
        }
    }

    /**
     * 处理接收到的数据
     */
    @Override
    public void onDataReceived(String topicName, Object data) {
        log.info("接收到数据: topic={}, data={}", topicName, data);

        try {
            switch (topicName) {
                case "Sensor/Temperature":
                    handleTemperatureData(data);
                    break;
                case "Sensor/Humidity":
                    handleHumidityData(data);
                    break;
                case "Control/Command":
                    handleControlCommand(data);
                    break;
                case "Alert/Message":
                    handleAlertMessage(data);
                    break;
                default:
                    log.warn("未知主题: {}", topicName);
            }
        } catch (Exception e) {
            log.error("处理数据失败: topic={}, data={}", topicName, data, e);
        }
    }

    /**
     * 处理温度数据
     */
    private void handleTemperatureData(Object data) {
        try {
            SensorData sensorData = null;

            if (data instanceof SensorData) {
                sensorData = (SensorData) data;
            } else if (data instanceof String) {
                // 尝试解析JSON字符串
                sensorData = objectMapper.readValue((String) data, SensorData.class);
            }

            if (sensorData != null) {
                // 更新统计数据
                updateDataStats("temperature", sensorData.getValue());

                // 检查阈值并创建警报
                checkThresholdAndCreateAlert(sensorData, TEMPERATURE_THRESHOLD_KEY);

                // 推送WebSocket消息
                webSocketService.pushTemperatureData(sensorData);

                log.debug("处理温度数据: {:.2f}°C", sensorData.getValue());
            } else {
                log.warn("无法解析温度数据: {}", data);
            }
        } catch (Exception e) {
            log.error("处理温度数据失败: {}", data, e);
        }
    }

    /**
     * 处理湿度数据
     */
    private void handleHumidityData(Object data) {
        try {
            SensorData sensorData = null;

            if (data instanceof SensorData) {
                sensorData = (SensorData) data;
            } else if (data instanceof String) {
                // 尝试解析JSON字符串
                sensorData = objectMapper.readValue((String) data, SensorData.class);
            }

            if (sensorData != null) {
                // 更新统计数据
                updateDataStats("humidity", sensorData.getValue());

                // 检查阈值并创建警报
                checkThresholdAndCreateAlert(sensorData, HUMIDITY_THRESHOLD_KEY);

                // 推送WebSocket消息
                webSocketService.pushHumidityData(sensorData);

                log.debug("处理湿度数据: {:.2f}%", sensorData.getValue());
            } else {
                log.warn("无法解析湿度数据: {}", data);
            }
        } catch (Exception e) {
            log.error("处理湿度数据失败: {}", data, e);
        }
    }

    /**
     * 处理控制命令
     */
    private void handleControlCommand(Object data) {
        log.info("处理控制命令: {}", data);

        try {
            // 根据命令类型执行相应操作
            if (data instanceof String) {
                String command = (String) data;
                executeControlCommand(command);
            } else {
                log.warn("接收到非字符串类型的控制命令: {}", data);
            }

            // 推送WebSocket消息
            webSocketService.pushSystemStatus(data);
        } catch (Exception e) {
            log.error("处理控制命令失败: {}", data, e);
        }
    }

    /**
     * 处理警报消息
     */
    private void handleAlertMessage(Object data) {
        log.info("处理警报消息: {}", data);

        try {
            if (data instanceof AlertMessage) {
                AlertMessage alert = (AlertMessage) data;

                // 保存警报到数据库
                alertMessageService.createAlert(alert);

                // 推送WebSocket消息
                webSocketService.pushAlertData(alert);

                log.info("警报消息处理完成: {}", alert.getTitle());
            } else {
                log.warn("接收到非AlertMessage类型的警报数据: {}", data);
            }
        } catch (Exception e) {
            log.error("处理警报消息失败: {}", data, e);
        }
    }

    /**
     * 执行控制命令
     */
    private void executeControlCommand(String command) {
        log.info("执行控制命令: {}", command);

        // 解析命令格式: COMMAND:PARAMETER
        String[] parts = command.split(":", 2);
        String cmd = parts[0].toUpperCase();
        String param = parts.length > 1 ? parts[1] : "";

        switch (cmd) {
            case "SET_TEMP_THRESHOLD":
                setTemperatureThreshold(param);
                break;
            case "SET_HUMIDITY_THRESHOLD":
                setHumidityThreshold(param);
                break;
            case "GENERATE_TEMP_DATA":
                generateTemperatureData();
                break;
            case "GENERATE_HUMIDITY_DATA":
                generateHumidityData();
                break;
            case "CLEAR_ALERTS":
                clearAllAlerts();
                break;
            case "RESET_STATS":
                resetAllStats();
                break;
            default:
                log.warn("未知控制命令: {}", cmd);
        }
    }

    /**
     * 设置温度阈值
     */
    private void setTemperatureThreshold(String value) {
        try {
            double threshold = Double.parseDouble(value);
            systemConfigService.saveConfig(TEMPERATURE_THRESHOLD_KEY, String.valueOf(threshold), "温度传感器阈值配置");
            log.info("温度阈值已更新为: {}", threshold);
        } catch (NumberFormatException e) {
            log.error("无效的温度阈值: {}", value);
        }
    }

    /**
     * 设置湿度阈值
     */
    private void setHumidityThreshold(String value) {
        try {
            double threshold = Double.parseDouble(value);
            systemConfigService.saveConfig(HUMIDITY_THRESHOLD_KEY, String.valueOf(threshold), "湿度传感器阈值配置");
            log.info("湿度阈值已更新为: {}", threshold);
        } catch (NumberFormatException e) {
            log.error("无效的湿度阈值: {}", value);
        }
    }

    /**
     * 生成温度数据
     */
    private void generateTemperatureData() {
        // 这里可以调用SensorSimulatorService来生成数据
        log.info("收到生成温度数据的命令");
    }

    /**
     * 生成湿度数据
     */
    private void generateHumidityData() {
        // 这里可以调用SensorSimulatorService来生成数据
        log.info("收到生成湿度数据的命令");
    }

    /**
     * 清除所有警报
     */
    private void clearAllAlerts() {
        alertMessageService.markAllAsRead();
        log.info("所有警报已标记为已读");
    }

    /**
     * 重置所有统计数据
     */
    private void resetAllStats() {
        resetStats("temperature");
        resetStats("humidity");
        log.info("所有统计数据已重置");
    }

    /**
     * 更新数据统计
     */
    private void updateDataStats(String sensorType, Double value) {
        dataCounters.computeIfAbsent(sensorType, k -> new AtomicInteger(0));
        dataSums.computeIfAbsent(sensorType, k -> 0.0);

        int count = dataCounters.get(sensorType).incrementAndGet();
        double sum = dataSums.get(sensorType) + value;
        dataSums.put(sensorType, sum);

        // 计算平均值
        double average = sum / count;

        // 如果数据点超过10个，检查平均值是否超过阈值
        if (count >= 10) {
            checkAverageThreshold(sensorType, average);

            // 重置计数器（保持最近10个数据点的平均值）
            dataCounters.get(sensorType).set(0);
            dataSums.put(sensorType, 0.0);
        }
    }

    /**
     * 检查平均值阈值
     */
    private void checkAverageThreshold(String sensorType, double average) {
        String thresholdKey = sensorType.equals("temperature") ? TEMPERATURE_THRESHOLD_KEY : HUMIDITY_THRESHOLD_KEY;

        systemConfigService.getDoubleConfig(thresholdKey).ifPresent(threshold -> {
            if (average > threshold) {
                // 创建警报
                AlertMessage alert = AlertMessage.builder()
                        .level(AlertMessage.AlertLevel.WARNING)
                        .title(sensorType.equals("temperature") ? "温度平均值警报" : "湿度平均值警报")
                        .content(String.format("%s平均值%.2f超过阈值%.2f",
                                sensorType.equals("temperature") ? "温度" : "湿度", average, threshold))
                        .sensorType(sensorType.equals("temperature") ? SensorData.SensorType.TEMPERATURE
                                : SensorData.SensorType.HUMIDITY)
                        .triggerValue(average)
                        .threshold(threshold)
                        .isRead(false)
                        .build();

                alertMessageService.createAlert(alert);
                webSocketService.pushAlertData(alert);

                log.warn("{}平均值超过阈值: {:.2f} > {:.2f}",
                        sensorType.equals("temperature") ? "温度" : "湿度", average, threshold);
            }
        });
    }

    /**
     * 检查阈值并创建警报
     */
    private void checkThresholdAndCreateAlert(SensorData sensorData, String thresholdKey) {
        systemConfigService.getDoubleConfig(thresholdKey).ifPresent(threshold -> {
            if (sensorData.getValue() > threshold) {
                AlertMessage.AlertLevel level = sensorData.getValue() > threshold * 1.2 ? AlertMessage.AlertLevel.ERROR
                        : AlertMessage.AlertLevel.WARNING;

                AlertMessage alert = alertMessageService.createSensorAlert(sensorData, threshold, level);
                webSocketService.pushAlertData(alert);

                log.warn("{}传感器数值超过阈值: {:.2f} > {:.2f}",
                        sensorData.getSensorType().getDescription(), sensorData.getValue(), threshold);
            }
        });
    }

    /**
     * 获取统计数据
     */
    public double getAverageValue(String sensorType) {
        AtomicInteger counter = dataCounters.get(sensorType);
        Double sum = dataSums.get(sensorType);

        if (counter != null && sum != null && counter.get() > 0) {
            return sum / counter.get();
        }
        return 0.0;
    }

    /**
     * 重置统计数据
     */
    public void resetStats(String sensorType) {
        dataCounters.remove(sensorType);
        dataSums.remove(sensorType);
        log.info("重置{}统计数据", sensorType);
    }

    /**
     * 获取ZRDDS服务状态
     */
    public boolean isZRDDSInitialized() {
        return zrddsService.isInitialized();
    }

    /**
     * 获取已订阅的主题列表
     */
    public java.util.Set<String> getSubscribedTopics() {
        return zrddsService.getSubscribedTopics();
    }
}
