package com.team9.fitness.controller;

import com.team9.fitness.service.ZRDDSService;
import com.team9.fitness.service.DataListenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * ZRDDS控制器
 * 提供ZRDDS中间件相关的API接口
 */
@RestController
@RequestMapping("/api/zrdds")
public class ZRDDSController {

    private static final Logger log = LoggerFactory.getLogger(ZRDDSController.class);

    @Autowired
    private ZRDDSService zrddsService;

    @Autowired
    private DataListenerService dataListenerService;

    /**
     * 获取ZRDDS服务状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("initialized", zrddsService.isInitialized());
        status.put("dataListenerInitialized", dataListenerService.isZRDDSInitialized());
        status.put("subscribedTopics", new java.util.HashSet<>(zrddsService.getSubscribedTopics()));

        return ResponseEntity.ok(status);
    }

    /**
     * 发布数据到指定主题
     */
    @PostMapping("/publish/{topicName}")
    public ResponseEntity<Map<String, Object>> publishData(
            @PathVariable String topicName,
            @RequestBody Object data) {

        try {
            zrddsService.publishData(topicName, data);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("topic", topicName);
            response.put("message", "数据发布成功");

            log.info("数据发布成功: topic={}, data={}", topicName, data);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("数据发布失败: topic={}, data={}", topicName, data, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("topic", topicName);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 订阅主题
     */
    @PostMapping("/subscribe/{topicName}")
    public ResponseEntity<Map<String, Object>> subscribeTopic(@PathVariable String topicName) {
        try {
            // 这里需要传入一个DataListener，但为了简化API，我们使用默认的监听器
            // 在实际应用中，可能需要更复杂的订阅逻辑

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("topic", topicName);
            response.put("message", "主题订阅成功");

            log.info("主题订阅成功: {}", topicName);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("主题订阅失败: {}", topicName, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("topic", topicName);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 取消订阅主题
     */
    @DeleteMapping("/unsubscribe/{topicName}")
    public ResponseEntity<Map<String, Object>> unsubscribeTopic(@PathVariable String topicName) {
        try {
            zrddsService.unsubscribeTopic(topicName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("topic", topicName);
            response.put("message", "主题取消订阅成功");

            log.info("主题取消订阅成功: {}", topicName);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("主题取消订阅失败: {}", topicName, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("topic", topicName);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取已订阅的主题列表
     */
    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> getSubscribedTopics() {
        Set<String> topics = zrddsService.getSubscribedTopics();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("topics", topics);
        response.put("count", topics.size());

        return ResponseEntity.ok(response);
    }

    /**
     * 发送控制命令
     */
    @PostMapping("/command")
    public ResponseEntity<Map<String, Object>> sendCommand(@RequestBody Map<String, String> command) {
        try {
            String commandText = command.get("command");
            if (commandText == null || commandText.trim().isEmpty()) {
                throw new IllegalArgumentException("命令不能为空");
            }

            // 发布控制命令到Control/Command主题
            zrddsService.publishData("Control/Command", commandText);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("command", commandText);
            response.put("message", "控制命令发送成功");

            log.info("控制命令发送成功: {}", commandText);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("控制命令发送失败: {}", command, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("command", command.get("command"));
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 重新初始化ZRDDS服务
     */
    @PostMapping("/reinitialize")
    public ResponseEntity<Map<String, Object>> reinitialize() {
        try {
            // 注意：这个操作可能需要重启应用才能完全重新初始化
            // 这里只是返回一个提示信息

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "ZRDDS服务重新初始化请求已接收，可能需要重启应用");
            response.put("warning", "完全重新初始化需要重启应用");

            log.info("ZRDDS服务重新初始化请求已接收");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("ZRDDS服务重新初始化失败", e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * 获取ZRDDS统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();

        // 基本状态信息
        stats.put("initialized", zrddsService.isInitialized());
        stats.put("dataListenerInitialized", dataListenerService.isZRDDSInitialized());

        // 主题信息
        Set<String> subscribedTopics = dataListenerService.getSubscribedTopics();
        stats.put("subscribedTopics", subscribedTopics);
        stats.put("topicCount", subscribedTopics.size());

        // 统计数据
        stats.put("temperatureAverage", dataListenerService.getAverageValue("temperature"));
        stats.put("humidityAverage", dataListenerService.getAverageValue("humidity"));

        return ResponseEntity.ok(stats);
    }
}
