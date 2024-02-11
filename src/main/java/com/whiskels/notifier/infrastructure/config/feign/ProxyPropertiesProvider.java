package com.whiskels.notifier.infrastructure.config.feign;


interface ProxyPropertiesProvider {
    String getUser();

    String getPassword();

    String getHost();

    int getPort();
}
