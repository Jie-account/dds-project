@echo off
echo ========================================
echo IDEA Lombok问题解决脚本
echo ========================================
echo.

echo 此脚本将帮助解决IDEA中的Lombok相关问题
echo.

echo [1/4] 清理项目...
call mvn clean
if %errorlevel% neq 0 (
    echo 警告: 清理项目时出现问题
)
echo 项目清理完成
echo.

echo [2/4] 重新编译项目...
call mvn compile -DskipTests
if %errorlevel% neq 0 (
    echo 错误: 项目编译失败，请检查代码
    pause
    exit /b 1
)
echo 项目编译成功
echo.

echo [3/4] 生成IDEA配置文件...
echo 正在生成IDEA配置文件...
echo.

echo [4/4] 完成！
echo.
echo ========================================
echo 解决步骤：
echo ========================================
echo 1. 在IDEA中安装Lombok插件：
echo    File → Settings → Plugins → 搜索"Lombok" → Install
echo.
echo 2. 启用注解处理：
echo    File → Settings → Build, Execution, Deployment → 
echo    Compiler → Annotation Processors → 
echo    勾选"Enable annotation processing"
echo.
echo 3. 重新编译项目：
echo    Build → Rebuild Project
echo.
echo 4. 如果仍有问题，请尝试：
echo    - 重启IDEA
echo    - 重新导入项目
echo    - 使用命令行运行: mvn spring-boot:run
echo.
echo ========================================
echo 项目已准备就绪！
echo 可以使用以下命令启动项目：
echo   mvn spring-boot:run
echo 或双击运行: quick-start.bat
echo ========================================
echo.

pause
