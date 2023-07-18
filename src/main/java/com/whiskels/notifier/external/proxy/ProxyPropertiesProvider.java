package com.whiskels.notifier.external.proxy;


interface ProxyPropertiesProvider {
    String getUser();

    String getPassword();

    String getHost();

    int getPort();
}
