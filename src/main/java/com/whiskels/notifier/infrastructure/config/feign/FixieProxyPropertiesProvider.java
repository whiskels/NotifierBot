package com.whiskels.notifier.infrastructure.config.feign;

import lombok.Getter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Getter
@Component
@ConditionalOnProperty(FixieProxyPropertiesProvider.FIXIE_ENV_VAR)
class FixieProxyPropertiesProvider implements ProxyPropertiesProvider {
    static final String FIXIE_ENV_VAR = "FIXIE_URL";
    static final String USER_ENV_VAR = "PROXIED_USER_NAME";
    static final String PASSWORD_ENV_VAR = "PROXIED_USER_PASSWORD";

    private final String proxyUser;
    private final String proxyPassword;
    private final String proxyHost;
    private final int proxyPort;
    private final String user;
    private final String password;

    public FixieProxyPropertiesProvider(final Environment environment) {
        final String fixieUrl = environment.getProperty(FIXIE_ENV_VAR);
        Objects.requireNonNull(fixieUrl);
        final String[] fixieValues = fixieUrl.split("[/(:\\/@)/]+");
        this.proxyUser = fixieValues[1];
        this.proxyPassword = fixieValues[2];
        this.proxyHost = fixieValues[3];
        this.proxyPort = Integer.parseInt(fixieValues[4]);
        this.user = environment.getProperty(USER_ENV_VAR);
        this.password = environment.getProperty(PASSWORD_ENV_VAR);
    }
}
