# ZRDDS集成总结

## 概述

本项目已成功集成ZRDDS中间件，实现了基于ZRDDS的传感器监控系统。ZRDDS是一个高性能的分布式数据服务中间件，支持实时数据发布订阅模式。

## 已完成的集成工作

### 1. ZRDDS服务层集成

#### 1.1 ZRDDSService类
- **位置**: `src/main/java/com/team9/fitness/service/ZRDDSService.java`
- **功能**: 
  - 基于ZRDDS简化接口实现
  - 管理ZRDDS中间件的初始化、清理
  - 提供数据发布和订阅功能
  - 支持主题管理

#### 1.2 主要功能
- **初始化**: 使用ZRDDS简化接口初始化中间件
- **域参与者管理**: 创建和管理域参与者
- **主题管理**: 支持4个默认主题的发布订阅
  - `Sensor/Temperature` - 温度传感器数据
  - `Sensor/Humidity` - 湿度传感器数据
  - `Control/Command` - 控制命令
  - `Alert/Message` - 警报消息
- **数据发布**: 支持向指定主题发布数据
- **数据订阅**: 支持订阅指定主题并接收数据
- **资源清理**: 应用关闭时自动清理ZRDDS资源

### 2. 数据监听器集成

#### 2.1 DataListenerService类
- **位置**: `src/main/java/com/team9/fitness/service/DataListenerService.java`
- **功能**:
  - 实现ZRDDSService.DataListener接口
  - 处理从ZRDDS接收到的数据
  - 支持控制命令执行
  - 集成警报系统

#### 2.2 支持的控制命令
- `SET_TEMP_THRESHOLD:value` - 设置温度阈值
- `SET_HUMIDITY_THRESHOLD:value` - 设置湿度阈值
- `GENERATE_TEMP_DATA` - 生成温度数据
- `GENERATE_HUMIDITY_DATA` - 生成湿度数据
- `CLEAR_ALERTS` - 清除所有警报
- `RESET_STATS` - 重置统计数据

### 3. 配置管理

#### 3.1 ZRDDS配置
- **配置文件**: `src/main/resources/application.yaml`
- **配置项**:
  ```yaml
  zrdds:
    domain-id: 0
    participant-name: FitnessParticipant
    qos-profile: ZRDDS_QOS_PROFILES.xml
  ```

#### 3.2 QoS配置文件
- **文件**: `src/main/resources/ZRDDS_QOS_PROFILES.xml`
- **功能**: 定义ZRDDS实体的QoS策略
- **配置内容**:
  - 域参与者QoS
  - 数据写者QoS (可靠性、历史记录等)
  - 数据读者QoS

### 4. API接口

#### 4.1 ZRDDS控制器
- **位置**: `src/main/java/com/team9/fitness/controller/ZRDDSController.java`
- **功能**: 提供ZRDDS相关的REST API接口

#### 4.2 主要API端点
- `GET /api/zrdds/status` - 获取ZRDDS服务状态
- `POST /api/zrdds/publish/{topicName}` - 发布数据到指定主题
- `POST /api/zrdds/subscribe/{topicName}` - 订阅指定主题
- `DELETE /api/zrdds/unsubscribe/{topicName}` - 取消订阅主题
- `GET /api/zrdds/topics` - 获取已订阅的主题列表
- `POST /api/zrdds/command` - 发送控制命令
- `POST /api/zrdds/reinitialize` - 重新初始化ZRDDS服务
- `GET /api/zrdds/stats` - 获取ZRDDS统计信息

### 5. 系统架构

#### 5.1 集成架构
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Web前端       │    │   Spring Boot   │    │   ZRDDS中间件   │
│                 │    │   应用服务       │    │                 │
│  - 实时监控     │◄──►│  - 业务逻辑     │◄──►│  - 数据发布     │
│  - 数据展示     │    │  - 数据监听     │    │  - 数据订阅     │
│  - 控制命令     │    │  - API接口      │    │  - 主题管理     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                              ▼
                       ┌─────────────────┐
                       │   MySQL数据库   │
                       │                 │
                       │  - 传感器数据   │
                       │  - 警报信息     │
                       │  - 系统配置     │
                       └─────────────────┘
```

#### 5.2 数据流
1. **传感器数据流**: 传感器 → ZRDDS → DataListenerService → 数据库 + WebSocket
2. **控制命令流**: Web前端 → API → ZRDDS → DataListenerService → 系统操作
3. **警报流**: 阈值检测 → 警报生成 → ZRDDS → WebSocket推送

## 使用方法

### 1. 启动应用
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 2. 访问监控界面
- **URL**: `http://localhost:8080`
- **功能**: 实时监控传感器数据、查看警报、发送控制命令

### 3. API使用示例

#### 3.1 发布传感器数据
```bash
curl -X POST http://localhost:8080/api/zrdds/publish/Sensor/Temperature \
  -H "Content-Type: application/json" \
  -d '{"value": 25.5, "unit": "°C", "timestamp": "2024-01-01T12:00:00"}'
```

#### 3.2 发送控制命令
```bash
curl -X POST http://localhost:8080/api/zrdds/command \
  -H "Content-Type: application/json" \
  -d '{"command": "SET_TEMP_THRESHOLD:30.0"}'
```

#### 3.3 查看ZRDDS状态
```bash
curl http://localhost:8080/api/zrdds/status
```

## 技术特点

### 1. 高性能
- 基于ZRDDS中间件，支持高并发数据发布订阅
- 使用简化接口，降低开发复杂度

### 2. 实时性
- WebSocket实时推送数据到前端
- ZRDDS低延迟数据传输

### 3. 可靠性
- 支持可靠传输QoS策略
- 数据持久化到MySQL数据库

### 4. 可扩展性
- 模块化设计，易于扩展新功能
- 支持动态主题管理

## 注意事项

### 1. ZRDDS库依赖
- 确保`lib/ZRDDS.jar`和`lib/ZRDDS_JAVA.dll`文件存在
- 当前使用模拟实现，实际部署时需要完整的ZRDDS库

### 2. 配置要求
- 确保MySQL数据库已启动并可连接
- 检查QoS配置文件路径是否正确

### 3. 性能调优
- 根据实际需求调整QoS配置
- 监控ZRDDS资源使用情况

## 后续优化建议

### 1. 完整ZRDDS集成
- 集成完整的ZRDDS库
- 实现真实的数据发布订阅功能

### 2. 性能优化
- 添加数据压缩
- 实现数据缓存机制

### 3. 监控增强
- 添加ZRDDS性能监控
- 实现数据质量检测

### 4. 安全增强
- 添加数据加密
- 实现访问控制

## 总结

ZRDDS集成工作已基本完成，实现了基于ZRDDS的传感器监控系统的核心功能。系统具备实时数据发布订阅、控制命令处理、警报管理等功能，为后续的功能扩展和性能优化奠定了良好的基础。
