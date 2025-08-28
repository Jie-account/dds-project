package com.team9.fitness.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;

/**
 * SSL/HTTPS配置
 */
@Configuration
public class SSLConfig {

    @Value("${server.http.port:8081}")
    private int httpPort;

    @Value("${server.port:8080}")
    private int httpsPort;

    /**
     * 配置Tomcat服务器，支持HTTP和HTTPS双端口
     */
    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();

        // 添加HTTP连接器（用于重定向到HTTPS）
        tomcat.addAdditionalTomcatConnectors(createHttpConnector());

        return tomcat;
    }

    /**
     * 创建HTTP连接器，将HTTP请求重定向到HTTPS
     */
    private Connector createHttpConnector() {
        Connector connector = new Connector(Http11NioProtocol.class.getName());
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setSecure(false);
        connector.setRedirectPort(httpsPort);

        return connector;
    }
}
