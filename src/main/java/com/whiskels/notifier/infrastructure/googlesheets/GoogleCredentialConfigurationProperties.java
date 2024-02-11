package com.whiskels.notifier.infrastructure.googlesheets;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = GoogleCredentialProvider.GOOGLE_CREDENTIALS_PREFIX)
class GoogleCredentialConfigurationProperties {
    private String appName;
    private String credentials;
    private String email;
}
