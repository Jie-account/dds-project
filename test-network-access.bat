@echo off
echo ========================================
echo 网络访问测试脚本
echo ========================================

echo.
echo [1/4] 检查本机IP地址...
ipconfig | findstr "IPv4"

echo.
echo [2/4] 检查8080端口是否监听...
netstat -an | findstr :8080

echo.
echo [3/4] 测试本地访问...
curl -s http://localhost:8080 > nul
if %errorlevel% equ 0 (
    echo ✓ 本地访问正常
) else (
    echo ✗ 本地访问失败
)

echo.
echo [4/4] 测试本机IP访问...
for /f "tokens=2 delims=:" %%i in ('ipconfig ^| findstr "IPv4" ^| findstr "192.168"') do (
    set LOCAL_IP=%%i
    set LOCAL_IP=!LOCAL_IP: =!
    echo 测试访问: http://!LOCAL_IP!:8080
    curl -s http://!LOCAL_IP!:8080 > nul
    if !errorlevel! equ 0 (
        echo ✓ 本机IP访问正常
    ) else (
        echo ✗ 本机IP访问失败
    )
)

echo.
echo ========================================
echo 测试完成
echo ========================================
echo.
echo 如果本机IP访问正常，其他成员可以通过以下地址访问：
echo http://[您的IP地址]:8080
echo.
pause
