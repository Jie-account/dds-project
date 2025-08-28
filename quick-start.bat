@echo off
echo ========================================
echo 基于ZRDDS的传感器监控系统 - 快速启动
echo ========================================
echo.

echo [1/4] 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Java环境，请先安装Java 17
    pause
    exit /b 1
)
echo Java环境检查完成
echo.

echo [2/4] 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven环境，请先安装Maven
    pause
    exit /b 1
)
echo Maven环境检查完成
echo.

echo [3/4] 编译项目...
mvn clean compile -DskipTests
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败
    pause
    exit /b 1
)
echo 项目编译成功
echo.

echo [4/4] 启动应用...
echo 应用正在启动，请稍候...
echo 启动完成后，请访问: http://localhost:8080
echo 按 Ctrl+C 停止应用
echo.

mvn spring-boot:run

pause
