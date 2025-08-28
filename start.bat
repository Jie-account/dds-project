@echo off
echo 启动传感器监控系统...
echo.

REM 检查Java环境
java -version >nul 2>&1
if errorlevel 1 (
    echo 错误: 未找到Java环境，请确保已安装Java 17或更高版本
    pause
    exit /b 1
)

REM 检查MySQL连接
echo 检查数据库连接...
java -cp "target/classes;target/dependency/*" com.team9.fitness.FitnessApplication --check-db >nul 2>&1
if errorlevel 1 (
    echo 警告: 数据库连接失败，请检查MySQL服务是否启动
    echo 按任意键继续启动（将使用内存数据库）...
    pause
)

REM 启动应用
echo 正在启动应用...
java -jar target/fitness-0.0.1-SNAPSHOT.jar

echo.
echo 应用已停止
pause
