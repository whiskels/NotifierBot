package com.whiskels.notifier.external.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@Lazy
@ConditionalOnProperty("external.google.credentials.json")
class GoogleCredentialProvider {
    public static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);

    @Getter
    private final String appName;
    @Getter
    private final NetHttpTransport httpTransport;
    private final String credentialsJson;
    private final String userEmail;

    @SneakyThrows
    GoogleCredentialProvider(@Value("${external.google.credentials.app.name}") String appName,
                             @Value("${external.google.credentials.json}") String credentialsJson,
                             @Value("${external.google.credentials.email}") String userEmail) {
        this.appName = appName;
        this.credentialsJson = credentialsJson;
        this.userEmail = userEmail;
        this.httpTransport = GoogleNetHttpTransport.newTrustedTransport();
    }

    @SneakyThrows
    public Credential getCredentials() {
        InputStream credentialsJSON = new ByteArrayInputStream(credentialsJson.getBytes());
        GoogleCredential gcFromJson = GoogleCredential.fromStream(credentialsJSON, httpTransport, JSON_FACTORY)
                .createScoped(SCOPES);

        return new GoogleCredential.Builder()
                .setTransport(gcFromJson.getTransport())
                .setJsonFactory(gcFromJson.getJsonFactory())
                .setServiceAccountId(gcFromJson.getServiceAccountId())
                .setServiceAccountUser(userEmail)
                .setServiceAccountPrivateKey(gcFromJson.getServiceAccountPrivateKey())
                .setServiceAccountScopes(gcFromJson.getServiceAccountScopes())
                .build();
    }
}
