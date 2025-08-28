# 传感器监控系统

基于ZRDDS中间件开发的传感器监控系统，支持实时监控温度、湿度传感器数据，并提供警报功能。

## 项目概述

本项目是一个基于Spring Boot的传感器监控系统，主要功能包括：

- 实时监控温度和湿度传感器数据
- 基于阈值的自动警报系统
- WebSocket实时数据推送
- RESTful API接口
- Web监控界面
- 数据持久化存储

## 技术栈

- **后端框架**: Spring Boot 3.5.5
- **数据库**: MySQL
- **ORM框架**: Spring Data JPA
- **实时通信**: WebSocket + STOMP
- **中间件**: ZRDDS (Zero-Runtime DDS)
- **构建工具**: Maven
- **Java版本**: 17

## 系统架构

### 核心组件

1. **传感器模拟器** (`SensorSimulatorService`)
   - 模拟温度传感器数据生成
   - 模拟湿度传感器数据生成
   - 可配置的数据生成间隔

2. **ZRDDS中间件服务** (`ZRDDSService`)
   - 负责ZRDDS中间件的初始化和管理
   - 提供数据发布和订阅功能
   - 支持4个主题：Sensor/Temperature、Sensor/Humidity、Control/Command、Alert/Message

3. **数据监听器** (`DataListenerService`)
   - 处理ZRDDS主题订阅
   - 实时数据处理和警报触发
   - 数据统计和平均值计算

4. **WebSocket服务** (`WebSocketService`)
   - 实时数据推送到前端
   - 支持多种数据类型的推送

### 数据模型

- **SensorData**: 传感器数据实体
- **AlertMessage**: 警报信息实体
- **SystemConfig**: 系统配置实体

### API接口

#### 传感器数据接口
- `GET /api/sensors/latest/{sensorType}` - 获取最新传感器数据
- `GET /api/sensors/{sensorType}/range` - 获取时间范围内的数据
- `GET /api/sensors/{sensorType}/recent` - 获取最近10条数据
- `GET /api/sensors/{sensorType}/average` - 计算平均值
- `POST /api/sensors/temperature/generate` - 手动生成温度数据
- `POST /api/sensors/humidity/generate` - 手动生成湿度数据

#### 警报接口
- `GET /api/alerts/unread` - 获取未读警报
- `GET /api/alerts/range` - 获取时间范围内的警报
- `GET /api/alerts/level/{level}` - 根据级别获取警报
- `PUT /api/alerts/{alertId}/read` - 标记警报为已读
- `PUT /api/alerts/read-all` - 标记所有警报为已读
- `GET /api/alerts/unread-count` - 获取未读警报数量

#### 系统配置接口
- `GET /api/system/config/{configKey}` - 获取配置值
- `POST /api/system/config` - 保存或更新配置
- `DELETE /api/system/config/{configKey}` - 删除配置

## 安装和运行

### 环境要求

- Java 17 或更高版本
- MySQL 5.7 或更高版本
- Maven 3.6 或更高版本

### 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE dds CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `application.yaml` 中的数据库连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/dds?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC&createDatabaseIfNotExist=true
    username: your_username
    password: your_password
```

### 编译和运行

1. 克隆项目：
```bash
git clone <repository-url>
cd fitness
```

2. 编译项目：
```bash
mvn clean compile
```

3. 运行项目：
```bash
mvn spring-boot:run
```

4. 访问Web界面：
```
http://localhost:8080
```

### 打包部署

1. 打包项目：
```bash
mvn clean package
```

2. 运行JAR文件：
```bash
java -jar target/fitness-0.0.1-SNAPSHOT.jar
```

## 配置说明

### 传感器配置

```yaml
sensor:
  temperature:
    interval: 5000  # 温度传感器数据发布间隔(毫秒)
    threshold: 30.0  # 温度阈值
  humidity:
    interval: 8000  # 湿度传感器数据发布间隔(毫秒)
    threshold: 80.0  # 湿度阈值
```

### ZRDDS配置

```yaml
zrdds:
  domain-id: 0
  participant-name: FitnessParticipant
  qos-profile: ZRDDS_QOS_PROFILES.xml
```

## 功能特性

### 实时监控
- 温度传感器数据实时显示
- 湿度传感器数据实时显示
- WebSocket实时数据推送
- 数据时间戳记录

### 警报系统
- 基于阈值的自动警报
- 多级别警报（信息、警告、错误、严重）
- 平均值阈值检测
- 警报历史记录

### 数据管理
- 数据持久化存储
- 历史数据查询
- 数据统计和分析
- 自动数据清理

### 系统配置
- 动态阈值配置
- 系统参数管理
- 配置持久化
- 配置验证

## Web界面功能

### 实时数据显示
- 温度传感器实时数值显示
- 湿度传感器实时数值显示
- 数据更新时间显示

### 警报管理
- 实时警报列表显示
- 警报级别标识
- 警报详情查看
- 一键标记已读

### 控制面板
- 手动生成传感器数据
- 刷新数据功能
- 系统状态监控

## 开发说明

### 项目结构
```
src/main/java/com/team9/fitness/
├── controller/          # 控制器层
├── entity/             # 数据实体
├── repository/         # 数据访问层
├── service/            # 业务逻辑层
└── config/             # 配置类
```

### 扩展开发

#### 添加新的传感器类型
1. 在 `SensorData.SensorType` 枚举中添加新类型
2. 在 `SensorSimulatorService` 中添加模拟逻辑
3. 在 `DataListenerService` 中添加处理逻辑
4. 更新Web界面显示

#### 添加新的警报规则
1. 在 `AlertMessageService` 中添加新的警报创建方法
2. 在 `DataListenerService` 中添加规则检查逻辑
3. 更新配置项

#### 集成真实的ZRDDS中间件
1. 在 `ZRDDSService` 中实现真实的ZRDDS API调用
2. 配置ZRDDS库路径和许可证
3. 测试数据发布和订阅功能

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接配置
   - 确认数据库用户权限

2. **ZRDDS中间件初始化失败**
   - 检查ZRDDS库文件是否存在
   - 验证许可证文件
   - 确认库路径配置

3. **WebSocket连接失败**
   - 检查防火墙设置
   - 验证WebSocket端点配置
   - 查看浏览器控制台错误

### 日志查看

项目使用SLF4J + Logback进行日志记录，日志级别可在 `application.yaml` 中配置：

```yaml
logging:
  level:
    com.team9.fitness: DEBUG
    com.zrdds: INFO
```

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题或建议，请联系开发团队。
