# 使用OpenJDK 17作为基础镜像
FROM openjdk:17-jdk-slim

# 设置工作目录
WORKDIR /app

# 复制Maven配置文件
COPY pom.xml .
COPY mvnw .
COPY mvnw.cmd .
COPY .mvn .mvn

# 复制源代码
COPY src src

# 设置Maven环境变量
ENV MAVEN_HOME=/usr/share/maven
ENV MAVEN_CONFIG_HOME=/root/.m2

# 安装Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# 构建应用
RUN mvn clean package -DskipTests

# 暴露端口
EXPOSE 8080

# 设置JVM参数
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar target/fitness-0.0.1-SNAPSHOT.jar"]
