-- 传感器监控系统数据库初始化脚本

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS dds CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE dds;

-- 创建传感器数据表
CREATE TABLE IF NOT EXISTS sensor_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sensor_type VARCHAR(20) NOT NULL,
    value DOUBLE NOT NULL,
    unit VARCHAR(20),
    created_at DATETIME NOT NULL,
    INDEX idx_sensor_type (sensor_type),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建警报信息表
CREATE TABLE IF NOT EXISTS alert_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    level VARCHAR(20) NOT NULL,
    title VARCHAR(200) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    sensor_type VARCHAR(20),
    trigger_value DOUBLE,
    threshold DOUBLE,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL,
    INDEX idx_level (level),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建系统配置表
CREATE TABLE IF NOT EXISTS system_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    config_key VARCHAR(100) NOT NULL UNIQUE,
    config_value VARCHAR(500) NOT NULL,
    description VARCHAR(200),
    updated_at DATETIME NOT NULL,
    INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认配置数据
INSERT IGNORE INTO system_config (config_key, config_value, description, updated_at) VALUES
('sensor.temperature.threshold', '30.0', '温度传感器阈值配置', NOW()),
('sensor.humidity.threshold', '80.0', '湿度传感器阈值配置', NOW()),
('system.data.retention.days', '30', '数据保留天数配置', NOW()),
('system.alert.level.default', 'WARNING', '默认警报级别配置', NOW()),
('system.name', 'Fitness Sensor Monitoring System', '系统名称配置', NOW()),
('system.version', '1.0.0', '系统版本配置', NOW());

-- 创建用户（如果不存在）
CREATE USER IF NOT EXISTS 'fitness'@'%' IDENTIFIED BY 'fitness123';
GRANT ALL PRIVILEGES ON dds.* TO 'fitness'@'%';
FLUSH PRIVILEGES;

-- 显示创建结果
SELECT 'Database initialization completed successfully!' as status;
