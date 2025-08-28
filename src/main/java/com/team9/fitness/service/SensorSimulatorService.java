package com.team9.fitness.service;

import com.team9.fitness.entity.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 传感器模拟器服务
 * 模拟温度传感器和湿度传感器的数据生成
 */
@Service
public class SensorSimulatorService {

    private static final Logger log = LoggerFactory.getLogger(SensorSimulatorService.class);

    @Autowired
    private SensorDataService sensorDataService;

    @Autowired
    private ZRDDSService zrddsService;

    @Value("${sensor.temperature.interval:5000}")
    private long temperatureInterval;

    @Value("${sensor.humidity.interval:8000}")
    private long humidityInterval;

    @Value("${sensor.temperature.threshold:30.0}")
    private double temperatureThreshold;

    @Value("${sensor.humidity.threshold:80.0}")
    private double humidityThreshold;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Random random = new Random();

    private Thread temperatureThread;
    private Thread humidityThread;

    /**
     * 启动传感器模拟器
     */
    @PostConstruct
    public void startSimulation() {
        if (running.compareAndSet(false, true)) {
            log.info("启动传感器模拟器...");

            // 启动温度传感器模拟线程
            temperatureThread = new Thread(this::simulateTemperatureSensor, "TemperatureSensor");
            temperatureThread.setDaemon(true);
            temperatureThread.start();

            // 启动湿度传感器模拟线程
            humidityThread = new Thread(this::simulateHumiditySensor, "HumiditySensor");
            humidityThread.setDaemon(true);
            humidityThread.start();

            log.info("传感器模拟器启动完成");
        }
    }

    /**
     * 停止传感器模拟器
     */
    @PreDestroy
    public void stopSimulation() {
        if (running.compareAndSet(true, false)) {
            log.info("停止传感器模拟器...");

            // 等待线程结束
            if (temperatureThread != null) {
                try {
                    temperatureThread.join(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            if (humidityThread != null) {
                try {
                    humidityThread.join(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            log.info("传感器模拟器已停止");
        }
    }

    /**
     * 模拟温度传感器
     */
    private void simulateTemperatureSensor() {
        log.info("温度传感器模拟线程启动，间隔: {}ms", temperatureInterval);

        while (running.get()) {
            try {
                // 生成模拟温度数据 (20-35度之间)
                double temperature = 20.0 + random.nextDouble() * 15.0;

                SensorData sensorData = SensorData.builder()
                        .sensorType(SensorData.SensorType.TEMPERATURE)
                        .value(temperature)
                        .unit("°C")
                        .build();

                // 保存到数据库
                sensorDataService.saveSensorData(sensorData);

                // 发布到ZRDDS主题
                zrddsService.publishData("Sensor/Temperature", sensorData);

                log.debug("生成温度数据: {:.2f}°C", temperature);

                Thread.sleep(temperatureInterval);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("温度传感器模拟出错", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("温度传感器模拟线程结束");
    }

    /**
     * 模拟湿度传感器
     */
    private void simulateHumiditySensor() {
        log.info("湿度传感器模拟线程启动，间隔: {}ms", humidityInterval);

        while (running.get()) {
            try {
                // 生成模拟湿度数据 (40-90%之间)
                double humidity = 40.0 + random.nextDouble() * 50.0;

                SensorData sensorData = SensorData.builder()
                        .sensorType(SensorData.SensorType.HUMIDITY)
                        .value(humidity)
                        .unit("%")
                        .build();

                // 保存到数据库
                sensorDataService.saveSensorData(sensorData);

                // 发布到ZRDDS主题
                zrddsService.publishData("Sensor/Humidity", sensorData);

                log.debug("生成湿度数据: {:.2f}%", humidity);

                Thread.sleep(humidityInterval);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("湿度传感器模拟出错", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        log.info("湿度传感器模拟线程结束");
    }

    /**
     * 检查模拟器是否正在运行
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * 手动生成一次温度数据
     */
    public SensorData generateTemperatureData() {
        double temperature = 20.0 + random.nextDouble() * 15.0;

        SensorData sensorData = SensorData.builder()
                .sensorType(SensorData.SensorType.TEMPERATURE)
                .value(temperature)
                .unit("°C")
                .build();

        sensorDataService.saveSensorData(sensorData);
        zrddsService.publishData("Sensor/Temperature", sensorData);

        log.info("手动生成温度数据: {:.2f}°C", temperature);
        return sensorData;
    }

    /**
     * 手动生成一次湿度数据
     */
    public SensorData generateHumidityData() {
        double humidity = 40.0 + random.nextDouble() * 50.0;

        SensorData sensorData = SensorData.builder()
                .sensorType(SensorData.SensorType.HUMIDITY)
                .value(humidity)
                .unit("%")
                .build();

        sensorDataService.saveSensorData(sensorData);
        zrddsService.publishData("Sensor/Humidity", sensorData);

        log.info("手动生成湿度数据: {:.2f}%", humidity);
        return sensorData;
    }
}
