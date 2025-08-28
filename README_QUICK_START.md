# 快速启动指南

## 🚀 快速启动

### 方法1：使用脚本启动（推荐）
```bash
# 双击运行以下脚本之一：
quick-start.bat          # 快速启动项目
check-status.bat         # 检查项目状态
fix-idea-issues.bat      # 解决IDEA问题
```

### 方法2：命令行启动
```bash
# 编译项目
mvn clean compile

# 启动应用
mvn spring-boot:run
```

### 方法3：IDEA中启动
1. 打开IDEA
2. 导入项目
3. 运行 `FitnessApplication.java`

## 🔧 IDEA Lombok问题解决

### 问题描述
在IDEA中启动时出现"找不到符号"错误：
```
java: 找不到符号
  符号:   方法 getLevel()
  位置: 类型为com.team9.fitness.entity.AlertMessage的变量 alertMessage
```

### 解决方案

#### 步骤1：安装Lombok插件
1. 打开IDEA
2. 进入 `File` → `Settings` → `Plugins`
3. 搜索"Lombok"并安装插件
4. 重启IDEA

#### 步骤2：启用注解处理
1. 进入 `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
2. 勾选 `Enable annotation processing`
3. 确保 `Obtain processors from project classpath` 已勾选
4. 点击 `Apply` 和 `OK`

#### 步骤3：重新编译
1. 执行 `Build` → `Rebuild Project`
2. 或者运行 `fix-idea-issues.bat`

### 备用方案
如果IDEA配置仍有问题：
```bash
# 使用Maven编译和运行
mvn clean compile
mvn spring-boot:run
```

## 📋 项目检查清单

### 环境要求
- ✅ Java 17+
- ✅ Maven 3.6+
- ✅ MySQL 8.0+
- ✅ IDEA 2021.1+ (可选)

### 启动前检查
1. **Java环境**：`java -version`
2. **Maven环境**：`mvn -version`
3. **MySQL服务**：确保MySQL服务已启动
4. **端口占用**：确保8080端口未被占用

### 运行脚本
```bash
# 检查项目状态
check-status.bat

# 快速启动
quick-start.bat

# 解决IDEA问题
fix-idea-issues.bat
```

## 🌐 访问应用

启动成功后，访问以下地址：

- **Web界面**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html (如果配置了Swagger)
- **健康检查**: http://localhost:8080/actuator/health

## 📁 项目结构

```
fitness/
├── src/main/java/com/team9/fitness/
│   ├── entity/          # 实体类
│   ├── repository/      # 数据访问层
│   ├── service/         # 业务逻辑层
│   ├── controller/      # 控制器层
│   └── config/          # 配置类
├── src/main/resources/
│   ├── application.yaml # 应用配置
│   ├── static/          # 静态资源
│   └── javadoc/         # ZRDDS文档
├── quick-start.bat      # 快速启动脚本
├── check-status.bat     # 状态检查脚本
├── fix-idea-issues.bat  # IDEA问题解决脚本
└── README.md            # 项目文档
```

## 🔍 常见问题

### Q1: IDEA中找不到Lombok生成的方法
**A**: 请按照上述IDEA配置步骤操作，或运行 `fix-idea-issues.bat`

### Q2: 数据库连接失败
**A**: 确保MySQL服务已启动，检查 `application.yaml` 中的数据库配置

### Q3: 端口8080被占用
**A**: 修改 `application.yaml` 中的端口配置，或停止占用端口的进程

### Q4: 编译失败
**A**: 运行 `check-status.bat` 检查环境，或查看详细错误信息

## 📞 技术支持

如果遇到问题，请：
1. 运行 `check-status.bat` 检查环境
2. 查看控制台错误信息
3. 参考项目文档
4. 检查日志文件

## 🎯 下一步

项目启动成功后，您可以：
1. 访问Web界面查看实时数据
2. 测试API接口
3. 配置传感器阈值
4. 查看警报信息
5. 集成ZRDDS中间件

---

**祝您使用愉快！** 🎉
