package com.whiskels.notifier.external.proxy;

import feign.Client;
import okhttp3.Authenticator;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

@ConditionalOnBean(ProxyPropertiesProvider.class)
public class FeignProxyConfig {
    @Bean
    public Client proxiedFeignClient(final ProxyPropertiesProvider propertiesProvider) {
        return new feign.okhttp.OkHttpClient(proxiedHttpClient(propertiesProvider));
    }

    private static OkHttpClient proxiedHttpClient(final ProxyPropertiesProvider propertiesProvider) {
        Authenticator proxyAuthenticator = (route, response) -> {
            String credential = Credentials.basic(propertiesProvider.getUser(), propertiesProvider.getPassword());
            return response.request().newBuilder()
                    .header("Proxy-Authorization", credential)
                    .build();
        };

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(propertiesProvider.getHost(), propertiesProvider.getPort()));

        return new OkHttpClient.Builder()
                .proxy(proxy)
                .proxyAuthenticator(proxyAuthenticator)
                .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                .build();
    }
}
