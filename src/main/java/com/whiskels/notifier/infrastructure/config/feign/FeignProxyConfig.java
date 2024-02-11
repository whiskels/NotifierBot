package com.whiskels.notifier.infrastructure.config.feign;

import feign.Client;
import okhttp3.Authenticator;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

public class FeignProxyConfig {
    @Value("${feign.proxy.max-idle-connections:5}")
    private int maxIdleConnections;
    @Value("${feign.proxy.keep-alive-duration-minutes:5}")
    private int keepAliveDurationMinutes;

    @Bean
    @ConditionalOnBean(ProxyPropertiesProvider.class)
    public Client proxiedFeignClient(final ProxyPropertiesProvider propertiesProvider) {
        return new feign.okhttp.OkHttpClient(
                proxiedHttpClient(propertiesProvider, maxIdleConnections, keepAliveDurationMinutes)
        );
    }

    private static OkHttpClient proxiedHttpClient(final ProxyPropertiesProvider propertiesProvider,
                                                  final int maxIdleConnections,
                                                  final int keepAliveDurationMinutes
    ) {
        Authenticator proxyAuthenticator = (_, response) -> {
            String credential = Credentials.basic(propertiesProvider.getUser(), propertiesProvider.getPassword());
            return response.request().newBuilder()
                    .header(HttpHeaders.PROXY_AUTHORIZATION, credential)
                    .build();
        };

        return new OkHttpClient.Builder()
                .proxy(createProxy(propertiesProvider))
                .proxyAuthenticator(proxyAuthenticator)
                .connectionPool(new ConnectionPool(maxIdleConnections, keepAliveDurationMinutes, TimeUnit.MINUTES))
                .build();
    }

    private static Proxy createProxy(ProxyPropertiesProvider propertiesProvider) {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(propertiesProvider.getHost(), propertiesProvider.getPort()));
    }
}
