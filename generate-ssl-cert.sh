#!/bin/bash

echo "正在生成SSL证书..."

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java，请先安装Java"
    exit 1
fi

# 创建证书目录
mkdir -p src/main/resources

# 生成自签名证书
echo "生成自签名SSL证书..."
keytool -genkeypair \
    -alias fitness \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore src/main/resources/keystore.p12 \
    -validity 3650 \
    -storepass fitness123 \
    -keypass fitness123 \
    -dname "CN=fitness.local, OU=Fitness Team, O=Fitness Company, L=Beijing, S=Beijing, C=CN"

if [ $? -ne 0 ]; then
    echo "错误: 证书生成失败"
    exit 1
fi

echo "SSL证书生成成功！"
echo "证书文件: src/main/resources/keystore.p12"
echo "证书密码: fitness123"
echo "证书别名: fitness"
echo ""
echo "注意: 这是自签名证书，Android客户端需要配置信任此证书"
