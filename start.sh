#!/bin/bash

echo "启动传感器监控系统..."
echo

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请确保已安装Java 17或更高版本"
    exit 1
fi

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 17 ]; then
    echo "错误: Java版本过低，需要Java 17或更高版本"
    exit 1
fi

# 检查MySQL连接
echo "检查数据库连接..."
if ! java -cp "target/classes:target/dependency/*" com.team9.fitness.FitnessApplication --check-db &> /dev/null; then
    echo "警告: 数据库连接失败，请检查MySQL服务是否启动"
    read -p "按回车键继续启动（将使用内存数据库）..."
fi

# 启动应用
echo "正在启动应用..."
java -jar target/fitness-0.0.1-SNAPSHOT.jar

echo
echo "应用已停止"
