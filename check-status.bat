@echo off
echo ========================================
echo 项目状态检查
echo ========================================
echo.

echo [1/5] 检查Java版本...
java -version 2>&1 | findstr "version"
if %errorlevel% neq 0 (
    echo ❌ Java未安装或未配置
) else (
    echo ✅ Java环境正常
)
echo.

echo [2/5] 检查Maven版本...
mvn -version 2>&1 | findstr "Apache Maven"
if %errorlevel% neq 0 (
    echo ❌ Maven未安装或未配置
) else (
    echo ✅ Maven环境正常
)
echo.

echo [3/5] 检查项目编译状态...
if exist "target\classes" (
    echo ✅ 项目已编译
) else (
    echo ⚠️  项目未编译，正在编译...
    call mvn compile -DskipTests -q
    if %errorlevel% neq 0 (
        echo ❌ 编译失败
    ) else (
        echo ✅ 编译成功
    )
)
echo.

echo [4/5] 检查数据库连接...
echo 请确保MySQL服务已启动，数据库配置正确
echo.

echo [5/5] 检查端口占用...
netstat -an | findstr ":8080"
if %errorlevel% neq 0 (
    echo ✅ 端口8080可用
) else (
    echo ⚠️  端口8080已被占用
)
echo.

echo ========================================
echo 检查完成！
echo ========================================
echo.
echo 如果所有检查都通过，可以启动项目：
echo   1. 双击运行: quick-start.bat
echo   2. 或使用命令: mvn spring-boot:run
echo.
echo 如果IDEA中有Lombok问题，请运行: fix-idea-issues.bat
echo.

pause
