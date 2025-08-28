# Android客户端配置指南

## 问题描述

Android客户端尝试连接HTTPS服务器时出现以下错误：
```
Invalid character found in method name [0x160x030x010x000xf60x010x000x000xf20x030x030xb7R0xf6]0xed0x08,0x9d0xdb0x0a0xf20xb10xa30xd00x9c0xad0x860xa60xf8xP0x9by0xf50xe40xe0.0xf90x18>0x8c0xae ]
```

这是因为Android客户端尝试建立HTTPS连接，但服务器只支持HTTP协议。

## 解决方案

### 方案一：使用HTTPS（推荐）

服务器已配置HTTPS支持，Android客户端需要相应配置：

#### 1. 服务器端配置

1. **生成SSL证书**：
   ```bash
   # Windows
   generate-ssl-cert.bat
   
   # Linux/Mac
   chmod +x generate-ssl-cert.sh
   ./generate-ssl-cert.sh
   ```

2. **启动服务器**：
   ```bash
   mvn spring-boot:run
   ```

3. **访问地址**：
   - HTTPS: `https://localhost:8080`
   - HTTP重定向: `http://localhost:8081` (自动重定向到HTTPS)

#### 2. Android客户端配置

##### 2.1 网络配置

在Android应用的网络配置中添加：

```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">10.0.2.2</domain> <!-- Android模拟器 -->
        <domain includeSubdomains="true">192.168.1.100</domain> <!-- 替换为实际IP -->
    </domain-config>
    
    <!-- 信任自签名证书 -->
    <domain-config>
        <domain includeSubdomains="true">localhost</domain>
        <trust-anchors>
            <certificates src="system"/>
            <certificates src="user"/>
        </trust-anchors>
    </domain-config>
</network-security-config>
```

##### 2.2 AndroidManifest.xml配置

```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    android:usesCleartextTraffic="true"
    ...>
    <!-- 其他配置 -->
</application>
```

##### 2.3 HTTP客户端配置

使用OkHttp或Retrofit时：

```java
// OkHttp配置
OkHttpClient client = new OkHttpClient.Builder()
    .hostnameVerifier((hostname, session) -> {
        // 允许所有主机名（仅用于开发环境）
        return true;
    })
    .build();

// 或者使用信任所有证书的配置（仅用于开发环境）
public static OkHttpClient getUnsafeOkHttpClient() {
    try {
        final TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[]{};
                }
            }
        };

        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
        builder.hostnameVerifier((hostname, session) -> true);

        return builder.build();
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
```

##### 2.4 API基础URL配置

```java
// 使用HTTPS
private static final String BASE_URL = "https://localhost:8080/api/";
// 或者使用实际IP地址
private static final String BASE_URL = "https://192.168.1.100:8080/api/";
```

### 方案二：使用HTTP（仅开发环境）

如果暂时不想配置HTTPS，可以修改服务器配置：

#### 1. 修改application.yaml

```yaml
server:
  port: 8080
  address: 0.0.0.0
  # 注释掉SSL配置
  # ssl:
  #   enabled: true
  #   key-store: classpath:keystore.p12
  #   key-store-password: fitness123
  #   key-store-type: PKCS12
  #   key-alias: fitness
```

#### 2. Android客户端配置

```xml
<!-- res/xml/network_security_config.xml -->
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">localhost</domain>
        <domain includeSubdomains="true">10.0.2.2</domain>
        <domain includeSubdomains="true">192.168.1.100</domain>
    </domain-config>
</network-security-config>
```

```java
// 使用HTTP
private static final String BASE_URL = "http://localhost:8080/api/";
```

## 测试连接

### 1. 服务器端测试

```bash
# 测试HTTPS连接
curl -k https://localhost:8080/api/sensors/latest/TEMPERATURE

# 测试HTTP重定向
curl -L http://localhost:8081/api/sensors/latest/TEMPERATURE
```

### 2. Android端测试

在Android应用中添加测试代码：

```java
public void testConnection() {
    String url = "https://localhost:8080/api/sensors/latest/TEMPERATURE";
    
    OkHttpClient client = getUnsafeOkHttpClient(); // 使用信任所有证书的客户端
    
    Request request = new Request.Builder()
        .url(url)
        .build();
    
    client.newCall(request).enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.e("NetworkTest", "连接失败: " + e.getMessage());
        }
        
        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String responseBody = response.body().string();
            Log.d("NetworkTest", "连接成功: " + responseBody);
        }
    });
}
```

## 常见问题

### 1. 证书信任问题

**错误**: `javax.net.ssl.SSLHandshakeException: java.security.cert.CertPathValidatorException`

**解决**: 使用上述的`getUnsafeOkHttpClient()`方法，或正确配置证书信任。

### 2. 网络权限问题

**错误**: `java.net.SocketException: Permission denied`

**解决**: 确保在AndroidManifest.xml中添加了网络权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 3. 主机名验证问题

**错误**: `javax.net.ssl.SSLPeerUnverifiedException: Hostname was not verified`

**解决**: 配置hostnameVerifier允许所有主机名。

## 生产环境注意事项

1. **不要使用自签名证书**：生产环境应使用受信任的CA签发的证书
2. **不要信任所有证书**：生产环境应正确配置证书验证
3. **使用HTTPS**：生产环境必须使用HTTPS协议
4. **网络安全配置**：根据实际需求配置网络安全策略

## 联系支持

如果遇到问题，请检查：
1. 服务器是否正常启动
2. 网络连接是否正常
3. 防火墙设置是否正确
4. 证书配置是否正确
