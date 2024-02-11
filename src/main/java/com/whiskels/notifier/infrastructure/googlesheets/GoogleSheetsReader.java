package com.whiskels.notifier.infrastructure.googlesheets;

import com.google.api.services.sheets.v4.Sheets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

import static com.whiskels.notifier.infrastructure.googlesheets.GoogleCredentialProvider.JSON_FACTORY;

@Slf4j
@Lazy
@Component
@RequiredArgsConstructor
public class GoogleSheetsReader {
    private final GoogleCredentialProvider credentialProvider;

    @Nonnull
    public List<List<Object>> read(@Nonnull final String spreadsheetId, @Nonnull final String range) {
        try {
            return getSheets()
                    .spreadsheets()
                    .values()
                    .get(spreadsheetId, range)
                    .execute()
                    .getValues();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Sheets getSheets() {
        return new Sheets.Builder(credentialProvider.getHttpTransport(), JSON_FACTORY, credentialProvider.getCredential())
                .setApplicationName(credentialProvider.getProperties().getAppName())
                .build();
    }
}
