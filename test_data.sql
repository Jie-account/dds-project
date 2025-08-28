-- 添加测试数据
USE dds;

-- 插入传感器数据
INSERT INTO sensor_data (sensor_type, value, unit, created_at) VALUES
('TEMPERATURE', 25.5, '°C', NOW() - INTERVAL 5 MINUTE),
('TEMPERATURE', 26.2, '°C', NOW() - INTERVAL 4 MINUTE),
('TEMPERATURE', 27.8, '°C', NOW() - INTERVAL 3 MINUTE),
('TEMPERATURE', 28.1, '°C', NOW() - INTERVAL 2 MINUTE),
('TEMPERATURE', 29.5, '°C', NOW() - INTERVAL 1 MINUTE),
('HUMIDITY', 65.2, '%', NOW() - INTERVAL 5 MINUTE),
('HUMIDITY', 67.8, '%', NOW() - INTERVAL 4 MINUTE),
('HUMIDITY', 70.1, '%', NOW() - INTERVAL 3 MINUTE),
('HUMIDITY', 72.5, '%', NOW() - INTERVAL 2 MINUTE),
('HUMIDITY', 75.3, '%', NOW() - INTERVAL 1 MINUTE);

-- 插入警报数据
INSERT INTO alert_message (level, title, content, sensor_type, trigger_value, threshold, is_read, created_at) VALUES
('WARNING', 'Temperature Alert', 'Temperature 29.5°C is approaching threshold 30.0°C', 'TEMPERATURE', 29.5, 30.0, FALSE, NOW() - INTERVAL 2 MINUTE),
('INFO', 'System Started', 'Sensor monitoring system has been initialized successfully', NULL, NULL, NULL, FALSE, NOW() - INTERVAL 10 MINUTE),
('WARNING', 'Humidity Alert', 'Humidity 75.3% is approaching threshold 80.0%', 'HUMIDITY', 75.3, 80.0, FALSE, NOW() - INTERVAL 1 MINUTE);

-- 显示插入的数据
SELECT 'Test data inserted successfully!' as status;
SELECT COUNT(*) as sensor_data_count FROM sensor_data;
SELECT COUNT(*) as alert_count FROM alert_message;
SELECT COUNT(*) as config_count FROM system_config;
