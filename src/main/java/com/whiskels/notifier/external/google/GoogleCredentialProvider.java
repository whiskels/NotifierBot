package com.whiskels.notifier.external.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;

import static com.whiskels.notifier.external.google.GoogleCredentialProvider.GOOGLE_CREDENTIALS_PREFIX;

@Lazy
@Slf4j
@Getter
@Component
@ConditionalOnProperty(prefix = GOOGLE_CREDENTIALS_PREFIX, name = {"app.name", "json", "email"})
@EnableConfigurationProperties(GoogleCredentialProvider.GoogleCredentialConfigurationProperties.class)
class GoogleCredentialProvider {
    private static final Collection<String> SCOPES = Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY);
    static final String GOOGLE_CREDENTIALS_PREFIX = "external.google.credentials";
    static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleCredentialConfigurationProperties properties;
    private final NetHttpTransport httpTransport;

    GoogleCredentialProvider(GoogleCredentialConfigurationProperties properties) throws GeneralSecurityException, IOException {
        this.properties = properties;
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    }

    @SneakyThrows
    public Credential getCredentials() {
        InputStream credentialsJSON = new ByteArrayInputStream(properties.getJson().getBytes());
        GoogleCredential gcFromJson = GoogleCredential.fromStream(credentialsJSON, httpTransport, JSON_FACTORY)
                .createScoped(SCOPES);

        return new GoogleCredential.Builder()
                .setTransport(gcFromJson.getTransport())
                .setJsonFactory(gcFromJson.getJsonFactory())
                .setServiceAccountId(gcFromJson.getServiceAccountId())
                .setServiceAccountUser(properties.getEmail())
                .setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey())
                .setServiceAccountScopes(gcFromJson.getServiceAccountScopes())
                .build();
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = GOOGLE_CREDENTIALS_PREFIX)
    static class GoogleCredentialConfigurationProperties {
        private String appName;
        private String json;
        private String email;
    }
}
