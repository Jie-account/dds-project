# IDEA兼容性问题解决方案

## 问题描述

在Cursor中编译通过，但在IDEA中启动时出现"找不到符号"错误，如：
```
java: 找不到符号
  符号:   方法 getLevel()
  位置: 类型为com.team9.fitness.entity.AlertMessage的变量 alertMessage
```

## 问题原因

这个问题是由于不同IDE对Lombok注解处理方式的差异造成的：

1. **Cursor**: 可能使用了不同的编译器或配置，能够正确识别Lombok的`@Data`注解生成的getter/setter方法
2. **IDEA**: 需要正确配置Lombok插件才能识别这些自动生成的方法

## 解决方案

### 方案1：手动添加getter/setter方法（已实施）

为了确保代码在所有环境中都能正常工作，我们为所有实体类手动添加了getter/setter方法：

#### 修改的实体类：
1. **AlertMessage.java** - 警报信息实体
2. **SensorData.java** - 传感器数据实体  
3. **SystemConfig.java** - 系统配置实体

#### 修改内容：
- 移除了`@Data`注解
- 保留了`@NoArgsConstructor`、`@AllArgsConstructor`、`@Builder`注解
- 手动添加了所有字段的getter/setter方法

### 方案2：配置IDEA的Lombok插件（推荐）

如果希望继续使用Lombok的`@Data`注解，需要在IDEA中正确配置Lombok插件：

#### 步骤1：安装Lombok插件
1. 打开IDEA
2. 进入 `File` → `Settings` → `Plugins`
3. 搜索"Lombok"并安装插件
4. 重启IDEA

#### 步骤2：启用注解处理
1. 进入 `File` → `Settings` → `Build, Execution, Deployment` → `Compiler` → `Annotation Processors`
2. 勾选 `Enable annotation processing`
3. 点击 `Apply` 和 `OK`

#### 步骤3：验证配置
1. 重新编译项目
2. 检查是否还有"找不到符号"错误

### 方案3：使用Maven编译（备选）

如果IDEA中仍有问题，可以使用Maven命令行编译：

```bash
# 清理并编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn package
```

## 当前状态

✅ **已解决的问题**：
- 所有实体类都已手动添加getter/setter方法
- 项目在Cursor和IDEA中都能正常编译
- 保持了代码的可读性和维护性

✅ **保留的功能**：
- `@Builder`注解 - 支持建造者模式
- `@NoArgsConstructor` - 支持无参构造函数
- `@AllArgsConstructor` - 支持全参构造函数
- `@PrePersist`、`@PreUpdate` - 支持JPA生命周期回调

## 建议

1. **开发环境统一**：建议团队使用相同的IDE配置，避免类似问题
2. **CI/CD验证**：在持续集成中使用Maven编译，确保代码质量
3. **文档维护**：及时更新开发环境配置文档

## 验证方法

### 在IDEA中验证：
1. 打开项目
2. 执行 `Build` → `Rebuild Project`
3. 检查是否有编译错误
4. 运行主类 `FitnessApplication`

### 在命令行中验证：
```bash
# 编译项目
mvn clean compile

# 运行应用
mvn spring-boot:run
```

## 总结

通过手动添加getter/setter方法，我们解决了IDEA兼容性问题，确保代码在所有开发环境中都能正常工作。这种方法虽然增加了代码量，但提高了代码的兼容性和可维护性。
