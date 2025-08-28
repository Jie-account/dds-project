# IDEA Lombok配置指南

## 问题描述

在IDEA中启动项目时出现"找不到符号"错误，如：
```
java: 找不到符号
  符号:   方法 getLevel()
  位置: 类型为com.team9.fitness.entity.AlertMessage的变量 alertMessage
```

这是因为IDEA没有正确配置Lombok插件导致的。

## 解决方案

### 步骤1：安装Lombok插件

1. 打开IDEA
2. 进入 `File` → `Settings` (或按 `Ctrl+Alt+S`)
3. 选择 `Plugins`
4. 在搜索框中输入"Lombok"
5. 找到"Lombok"插件并点击"Install"
6. 重启IDEA

### 步骤2：启用注解处理

1. 进入 `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
2. 勾选 `Enable annotation processing`
3. 确保 `Obtain processors from project classpath` 已勾选
4. 点击 `Apply` 和 `OK`

### 步骤3：重新编译项目

1. 执行 `Build` → `Rebuild Project`
2. 或者执行 `Build` → `Clean Project` 然后 `Build` → `Build Project`

### 步骤4：验证配置

1. 打开任意实体类（如 `AlertMessage.java`）
2. 检查是否能识别Lombok生成的方法（如 `getLevel()`, `setLevel()` 等）
3. 检查是否能识别 `builder()` 方法

## 常见问题解决

### 问题1：插件安装后仍然报错

**解决方案**：
1. 确保已重启IDEA
2. 检查 `File` → `Settings` → `Plugins` 中Lombok插件是否已启用
3. 重新编译项目

### 问题2：注解处理已启用但仍然报错

**解决方案**：
1. 清理项目：`Build` → `Clean Project`
2. 删除 `.idea` 文件夹中的缓存文件
3. 重新导入项目
4. 重新编译项目

### 问题3：Maven编译成功但IDEA报错

**解决方案**：
1. 使用Maven编译：`mvn clean compile`
2. 在IDEA中重新导入Maven项目：`File` → `Reload All Maven Projects`
3. 重新编译项目

## 验证方法

### 方法1：检查实体类
打开 `AlertMessage.java`，应该能看到：
- 自动生成的getter/setter方法
- `builder()` 方法
- 没有红色错误提示

### 方法2：运行测试
```bash
# 在命令行中运行
mvn clean compile
mvn spring-boot:run
```

### 方法3：IDEA中运行
1. 找到 `FitnessApplication.java`
2. 右键选择 "Run 'FitnessApplication'"
3. 检查控制台输出是否有错误

## 配置文件说明

项目已包含以下配置文件来帮助IDEA正确识别Lombok：

### `.idea/compiler.xml`
- 启用注解处理
- 配置Maven默认注解处理器配置文件

### `pom.xml`
- 包含Lombok依赖
- 配置了正确的版本

## 备用方案

如果IDEA配置仍有问题，可以使用以下备用方案：

### 方案1：使用Maven编译
```bash
mvn clean compile
mvn spring-boot:run
```

### 方案2：使用命令行运行
```bash
# 编译
mvn clean compile

# 运行
java -jar target/fitness-0.0.1-SNAPSHOT.jar
```

### 方案3：使用Docker
```bash
# 构建镜像
docker build -t fitness-app .

# 运行容器
docker run -p 8080:8080 fitness-app
```

## 总结

通过正确配置IDEA的Lombok插件和注解处理，可以解决"找不到符号"的错误。如果配置仍有问题，可以使用Maven命令行编译作为备用方案。

## 联系支持

如果按照以上步骤仍然无法解决问题，请：
1. 检查IDEA版本是否支持Lombok插件
2. 确认Maven依赖是否正确下载
3. 尝试重新导入项目
