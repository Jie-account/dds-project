package com.team9.fitness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * ZRDDS中间件服务
 * 基于ZRDDS简化接口实现
 */
@Service
public class ZRDDSService {

    private static final Logger log = LoggerFactory.getLogger(ZRDDSService.class);

    @Value("${zrdds.domain-id:0}")
    private int domainId;

    @Value("${zrdds.participant-name:FitnessParticipant}")
    private String participantName;

    @Value("${zrdds.qos-profile:ZRDDS_QOS_PROFILES.xml}")
    private String qosProfile;

    private boolean initialized = false;

    // 存储域参与者和发布订阅者的引用
    private com.zrdds.domain.DomainParticipant domainParticipant;
    private final Map<String, com.zrdds.publication.DataWriter> publishers = new ConcurrentHashMap<>();
    private final Map<String, com.zrdds.subscription.DataReader> subscribers = new ConcurrentHashMap<>();
    private final Map<String, DataListener> listeners = new ConcurrentHashMap<>();

    /**
     * 初始化ZRDDS中间件
     */
    @PostConstruct
    public void initialize() {
        try {
            log.info("初始化ZRDDS中间件...");
            log.info("Domain ID: {}", domainId);
            log.info("Participant Name: {}", participantName);
            log.info("QoS Profile: {}", qosProfile);

            // 1. 初始化ZRDDS简化接口
            initZRDDS();

            // 2. 创建域参与者
            createDomainParticipant();

            // 3. 初始化默认主题
            initializeDefaultTopics();

            initialized = true;
            log.info("ZRDDS中间件初始化成功");

        } catch (Exception e) {
            log.error("ZRDDS中间件初始化失败", e);
            throw new RuntimeException("ZRDDS中间件初始化失败", e);
        }
    }

    /**
     * 初始化ZRDDS简化接口
     */
    private void initZRDDS() {
        try {
            log.info("开始初始化ZRDDS简化接口...");
            log.info("QoS配置文件路径: {}", qosProfile);
            log.info("QoS库名称: default_lib");

            // 检查QoS配置文件是否存在
            java.io.File qosFile = new java.io.File(qosProfile);
            if (!qosFile.exists()) {
                log.error("QoS配置文件不存在: {}", qosFile.getAbsolutePath());
                throw new RuntimeException("QoS配置文件不存在: " + qosFile.getAbsolutePath());
            }
            log.info("QoS配置文件存在: {}", qosFile.getAbsolutePath());

            // 使用ZRDDS简化接口初始化
            // 第一个参数是QoS配置文件路径，第二个参数是库名称
            com.zrdds.simpleinterface.DDSIF.init(qosProfile, "default_lib");
            log.info("ZRDDS简化接口初始化成功");
        } catch (Exception e) {
            log.error("ZRDDS简化接口初始化失败", e);
            log.error("错误详情: {}", e.getMessage());
            if (e.getCause() != null) {
                log.error("根本原因: {}", e.getCause().getMessage());
            }
            throw new RuntimeException("ZRDDS简化接口初始化失败", e);
        }
    }

    /**
     * 创建域参与者
     */
    private void createDomainParticipant() {
        try {
            // 创建UDP域参与者
            domainParticipant = com.zrdds.simpleinterface.DDSIF.create_dp(domainId, "udp_dp");
            if (domainParticipant == null) {
                throw new RuntimeException("创建域参与者失败");
            }
            log.info("域参与者创建成功: domainId={}, participantName={}", domainId, participantName);
        } catch (Exception e) {
            log.error("创建域参与者失败", e);
            throw new RuntimeException("创建域参与者失败", e);
        }
    }

    /**
     * 初始化默认主题
     */
    private void initializeDefaultTopics() {
        String[] defaultTopics = { "Sensor/Temperature", "Sensor/Humidity", "Control/Command", "Alert/Message" };

        for (String topic : defaultTopics) {
            try {
                // 为每个主题创建发布者和订阅者
                createTopicPublisher(topic);
                createTopicSubscriber(topic);

                log.info("主题 {} 的发布者和订阅者创建成功", topic);
            } catch (Exception e) {
                log.error("创建主题 {} 的发布者/订阅者失败", topic, e);
            }
        }
    }

    /**
     * 创建主题发布者
     */
    private void createTopicPublisher(String topicName) {
        try {
            // 使用ZRDDS简化接口创建发布者，使用BytesTypeSupport
            com.zrdds.publication.DataWriter publisher = com.zrdds.simpleinterface.DDSIF.pub_topic(
                    domainParticipant, topicName, com.zrdds.infrastructure.BytesTypeSupport.get_instance(), "reliable", null);
            if (publisher != null) {
                publishers.put(topicName, publisher);
                log.debug("主题发布者创建成功: {}", topicName);
            } else {
                log.error("创建主题发布者失败: {}", topicName);
            }
        } catch (Exception e) {
            log.error("创建主题发布者失败: {}", topicName, e);
            throw new RuntimeException("创建主题发布者失败", e);
        }
    }

    /**
     * 创建主题订阅者
     */
    private void createTopicSubscriber(String topicName) {
        try {
            // 创建数据监听器
            ZRDDSDataListener dataListener = new ZRDDSDataListener(topicName, null);
            
            // 使用ZRDDS简化接口创建订阅者，使用BytesTypeSupport和真实的数据监听器
            com.zrdds.subscription.DataReader subscriber = com.zrdds.simpleinterface.DDSIF.sub_topic(
                    domainParticipant, topicName, com.zrdds.infrastructure.BytesTypeSupport.get_instance(), "reliable", dataListener);
            if (subscriber != null) {
                subscribers.put(topicName, subscriber);
                log.debug("主题订阅者创建成功: {}", topicName);
            } else {
                log.error("创建主题订阅者失败: {}", topicName);
            }
        } catch (Exception e) {
            log.error("创建主题订阅者失败: {}", topicName, e);
            throw new RuntimeException("创建主题订阅者失败", e);
        }
    }

    /**
     * 清理ZRDDS中间件资源
     */
    @PreDestroy
    public void cleanup() {
        if (initialized) {
            try {
                log.info("清理ZRDDS中间件资源...");

                // 1. 取消所有订阅
                cleanupSubscribers();

                // 2. 取消所有发布
                cleanupPublishers();

                // 3. 清理ZRDDS简化接口
                cleanupZRDDS();

                initialized = false;
                log.info("ZRDDS中间件资源清理完成");

            } catch (Exception e) {
                log.error("ZRDDS中间件资源清理失败", e);
            }
        }
    }

    /**
     * 清理订阅者
     */
    private void cleanupSubscribers() {
        for (String topicName : subscribers.keySet()) {
            try {
                com.zrdds.subscription.DataReader subscriber = subscribers.get(topicName);
                com.zrdds.simpleinterface.DDSIF.unsub_topic(subscriber);
                log.debug("取消订阅主题: {}", topicName);
            } catch (Exception e) {
                log.error("取消订阅主题失败: {}", topicName, e);
            }
        }
        subscribers.clear();
        listeners.clear();
        log.info("所有订阅者清理完成");
    }

    /**
     * 清理发布者
     */
    private void cleanupPublishers() {
        for (String topicName : publishers.keySet()) {
            try {
                com.zrdds.publication.DataWriter publisher = publishers.get(topicName);
                com.zrdds.simpleinterface.DDSIF.unpub_topic(publisher);
                log.debug("取消发布主题: {}", topicName);
            } catch (Exception e) {
                log.error("取消发布主题失败: {}", topicName, e);
            }
        }
        publishers.clear();
        log.info("所有发布者清理完成");
    }

    /**
     * 清理ZRDDS简化接口
     */
    private void cleanupZRDDS() {
        try {
            // 清理ZRDDS简化接口
            com.zrdds.simpleinterface.DDSIF.Finalize();
            log.info("ZRDDS简化接口清理完成");
        } catch (Exception e) {
            log.error("清理ZRDDS简化接口失败", e);
        }
    }

    /**
     * 检查ZRDDS中间件是否已初始化
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 发布数据到指定主题
     */
    public void publishData(String topicName, Object data) {
        if (!initialized) {
            throw new IllegalStateException("ZRDDS中间件未初始化");
        }

        try {
            log.debug("发布数据到主题: {}, 数据: {}", topicName, data);

            // 1. 获取对应主题的发布者
            com.zrdds.publication.DataWriter publisher = publishers.get(topicName);
            if (publisher == null) {
                // 如果发布者不存在，创建一个
                createTopicPublisher(topicName);
                publisher = publishers.get(topicName);
            }

            // 2. 将数据序列化
            byte[] serializedData = serializeData(data);

            // 3. 发布数据
            publishDataToTopic(publisher, serializedData);

            log.debug("数据发布成功: topic={}, dataSize={}", topicName, serializedData.length);

        } catch (Exception e) {
            log.error("发布数据失败: topic={}, data={}", topicName, data, e);
            throw new RuntimeException("发布数据失败", e);
        }
    }

    /**
     * 序列化数据
     */
    private byte[] serializeData(Object data) {
        // 模拟数据序列化
        if (data instanceof String) {
            return ((String) data).getBytes();
        } else {
            return data.toString().getBytes();
        }
    }

    /**
     * 发布数据到主题
     */
    private void publishDataToTopic(com.zrdds.publication.DataWriter publisher, byte[] data) {
        try {
            // 使用ZRDDS简化接口发布数据
            // 方法1：使用BytesWrite直接发送
            com.zrdds.simpleinterface.DDSIF.BytesWrite(domainId, getTopicNameFromPublisher(publisher), data, data.length);
            log.debug("数据发布成功: size={}", data.length);
        } catch (Exception e) {
            log.error("发布数据到主题失败", e);
            throw new RuntimeException("发布数据到主题失败", e);
        }
    }
    
    /**
     * 从发布者获取主题名称
     */
    private String getTopicNameFromPublisher(com.zrdds.publication.DataWriter publisher) {
        // 遍历发布者映射找到对应的主题名称
        for (Map.Entry<String, com.zrdds.publication.DataWriter> entry : publishers.entrySet()) {
            if (entry.getValue() == publisher) {
                return entry.getKey();
            }
        }
        return "unknown_topic";
    }

    /**
     * 订阅指定主题
     */
    public void subscribeTopic(String topicName, DataListener listener) {
        if (!initialized) {
            throw new IllegalStateException("ZRDDS中间件未初始化");
        }

        try {
            log.info("订阅主题: {}", topicName);

            // 1. 创建对应主题的订阅者（如果不存在）
            com.zrdds.subscription.DataReader subscriber = subscribers.get(topicName);
            if (subscriber == null) {
                // 创建带有监听器的订阅者
                createTopicSubscriberWithListener(topicName, listener);
                subscriber = subscribers.get(topicName);
            } else {
                // 如果订阅者已存在，更新监听器
                listeners.put(topicName, listener);
            }

            log.info("主题 {} 订阅成功", topicName);

        } catch (Exception e) {
            log.error("订阅主题失败: topic={}", topicName, e);
            throw new RuntimeException("订阅主题失败", e);
        }
    }
    
    /**
     * 创建带有监听器的主题订阅者
     */
    private void createTopicSubscriberWithListener(String topicName, DataListener listener) {
        try {
            // 创建数据监听器
            ZRDDSDataListener dataListener = new ZRDDSDataListener(topicName, listener);
            
            // 使用ZRDDS简化接口创建订阅者
            com.zrdds.subscription.DataReader subscriber = com.zrdds.simpleinterface.DDSIF.sub_topic(
                    domainParticipant, topicName, com.zrdds.infrastructure.BytesTypeSupport.get_instance(), "reliable", dataListener);
            if (subscriber != null) {
                subscribers.put(topicName, subscriber);
                listeners.put(topicName, listener);
                log.debug("主题订阅者创建成功: {}", topicName);
            } else {
                log.error("创建主题订阅者失败: {}", topicName);
            }
        } catch (Exception e) {
            log.error("创建主题订阅者失败: {}", topicName, e);
            throw new RuntimeException("创建主题订阅者失败", e);
        }
    }



    /**
     * 取消订阅主题
     */
    public void unsubscribeTopic(String topicName) {
        if (initialized) {
            try {
                com.zrdds.subscription.DataReader subscriber = subscribers.get(topicName);
                if (subscriber != null) {
                    com.zrdds.simpleinterface.DDSIF.unsub_topic(subscriber);
                    subscribers.remove(topicName);
                }
                listeners.remove(topicName);
                log.info("取消订阅主题: {}", topicName);
            } catch (Exception e) {
                log.error("取消订阅主题失败: {}", topicName, e);
            }
        }
    }

    /**
     * 获取已订阅的主题列表
     */
    public java.util.Set<String> getSubscribedTopics() {
        return listeners.keySet();
    }

    /**
     * 数据监听器接口
     */
    public interface DataListener {
        void onDataReceived(String topicName, Object data);
    }
}
