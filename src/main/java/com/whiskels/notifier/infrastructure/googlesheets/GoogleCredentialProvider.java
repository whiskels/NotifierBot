package com.whiskels.notifier.infrastructure.googlesheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.Collections;

@Lazy
@Getter
@Component
@ConfigurationPropertiesScan
@AllArgsConstructor
class GoogleCredentialProvider {
    private static final Collection<String> SCOPES = Collections.singleton(SheetsScopes.SPREADSHEETS_READONLY);
    static final String GOOGLE_CREDENTIALS_PREFIX = "google.spreadsheets.parameters";
    static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private final GoogleCredentialConfigurationProperties properties;
    private final NetHttpTransport httpTransport;
    private final Credential credential;

    @Autowired
    GoogleCredentialProvider(GoogleCredentialConfigurationProperties properties) throws GeneralSecurityException, IOException {
        this.properties = properties;
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        this.credential = initializeCredential();
    }

    @SneakyThrows
    private Credential initializeCredential() {
        InputStream credentialsJSON = new ByteArrayInputStream(properties.getCredentials().getBytes());
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
}
