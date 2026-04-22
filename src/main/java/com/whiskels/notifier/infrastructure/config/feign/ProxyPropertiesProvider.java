package com.whiskels.notifier.infrastructure.config.feign;


interface ProxyPropertiesProvider {
    String getProxyUser();

    String getProxyPassword();

    String getProxyHost();

    int getProxyPort();

    String getUser();

    String getPassword();
}
