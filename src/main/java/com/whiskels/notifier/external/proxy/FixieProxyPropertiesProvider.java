package com.whiskels.notifier.external.proxy;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnProperty(FixieProxyPropertiesProvider.FIXIE_ENV_VAR)
@Getter
class FixieProxyPropertiesProvider implements ProxyPropertiesProvider {
    public static final String FIXIE_ENV_VAR = "FIXIE_URL";

    private final String user;
    private final String password;
    private final String host;
    private final int port;

    public FixieProxyPropertiesProvider(final Environment environment) {
        final String fixieUrl = environment.getProperty("FIXIE_URL");
        Objects.requireNonNull(fixieUrl);
        final String[] fixieValues = fixieUrl.split("[/(:\\/@)/]+");
        this.user = fixieValues[1];
        this.password = fixieValues[2];
        this.host = fixieValues[3];
        this.port = Integer.parseInt(fixieValues[4]);
    }
}
