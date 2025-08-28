@echo off
echo 打包传感器监控系统...
echo.

REM 清理之前的构建
echo 清理之前的构建...
call mvn clean

REM 编译项目
echo 编译项目...
call mvn compile

REM 运行测试
echo 运行测试...
call mvn test

REM 打包项目
echo 打包项目...
call mvn package -DskipTests

REM 检查打包结果
if exist "target\fitness-0.0.1-SNAPSHOT.jar" (
    echo.
    echo 打包成功！
    echo JAR文件位置: target\fitness-0.0.1-SNAPSHOT.jar
    echo.
    echo 可以使用以下命令启动应用：
    echo java -jar target\fitness-0.0.1-SNAPSHOT.jar
    echo.
    echo 或者直接运行 start.bat
) else (
    echo.
    echo 打包失败！
    echo 请检查错误信息并修复问题
)

pause
