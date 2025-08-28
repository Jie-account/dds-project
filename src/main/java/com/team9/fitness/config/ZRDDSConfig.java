package com.team9.fitness.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * ZRDDS配置类
 * 负责ZRDDS中间件的配置和初始化
 */
@Configuration
public class ZRDDSConfig {

    private static final Logger log = LoggerFactory.getLogger(ZRDDSConfig.class);

    @Value("${zrdds.domain-id:0}")
    private int domainId;

    @Value("${zrdds.participant-name:FitnessParticipant}")
    private String participantName;

    @Value("${zrdds.qos-profile:ZRDDS_QOS_PROFILES.xml}")
    private String qosProfile;

    /**
     * 初始化ZRDDS环境
     */
    @Bean
    public ZRDDSConfig initializeZRDDSEnvironment() {
        log.info("初始化ZRDDS环境...");

        try {
            // 设置ZRDDS库路径
            setupZRDDSLibraryPath();

            // 验证QoS配置文件
            validateQoSProfile();

            log.info("ZRDDS环境初始化完成");
            log.info("Domain ID: {}", domainId);
            log.info("Participant Name: {}", participantName);
            log.info("QoS Profile: {}", qosProfile);

        } catch (Exception e) {
            log.error("ZRDDS环境初始化失败", e);
            throw new RuntimeException("ZRDDS环境初始化失败", e);
        }
        
        return this;
    }

    /**
     * 设置ZRDDS库路径
     */
    private void setupZRDDSLibraryPath() {
        try {
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            String libPath = projectRoot + File.separator + "lib";

            // 设置库路径
            System.setProperty("java.library.path", libPath);

            // 验证库文件是否存在
            File zrddsJar = new File(libPath + File.separator + "ZRDDS.jar");
            File zrddsDll = new File(libPath + File.separator + "ZRDDS_JAVA.dll");

            if (!zrddsJar.exists()) {
                log.warn("ZRDDS.jar文件不存在: {}", zrddsJar.getAbsolutePath());
            } else {
                log.info("ZRDDS.jar文件存在: {}", zrddsJar.getAbsolutePath());
            }

            if (!zrddsDll.exists()) {
                log.warn("ZRDDS_JAVA.dll文件不存在: {}", zrddsDll.getAbsolutePath());
            } else {
                log.info("ZRDDS_JAVA.dll文件存在: {}", zrddsDll.getAbsolutePath());
            }

        } catch (Exception e) {
            log.error("设置ZRDDS库路径失败", e);
        }
    }

    /**
     * 验证QoS配置文件
     */
    private void validateQoSProfile() {
        try {
            // 检查QoS配置文件是否存在
            File qosFile = new File(qosProfile);
            if (!qosFile.exists()) {
                log.warn("QoS配置文件不存在: {}", qosProfile);
                log.info("将使用默认QoS配置");
            } else {
                log.info("QoS配置文件存在: {}", qosFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("验证QoS配置文件失败", e);
        }
    }

    /**
     * 获取Domain ID
     */
    public int getDomainId() {
        return domainId;
    }

    /**
     * 获取Participant Name
     */
    public String getParticipantName() {
        return participantName;
    }

    /**
     * 获取QoS Profile
     */
    public String getQosProfile() {
        return qosProfile;
    }
}
