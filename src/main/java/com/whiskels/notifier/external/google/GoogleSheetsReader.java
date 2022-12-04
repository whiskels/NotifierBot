package com.whiskels.notifier.external.google;

import com.google.api.services.sheets.v4.Sheets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.whiskels.notifier.external.google.GoogleCredentialProvider.JSON_FACTORY;
import static java.util.Collections.emptyList;

@Slf4j
@Lazy
@Component
@RequiredArgsConstructor
public class GoogleSheetsReader {
    private final GoogleCredentialProvider credentialProvider;

    public List<List<Object>> read(String spreadsheetId, String range) {
        try {
            return getSheets()
                    .spreadsheets()
                    .values()
                    .get(spreadsheetId, range)
                    .execute()
                    .getValues();
        } catch (Exception e) {
            log.error("Unable to retrieve data from spreadsheet: {}", e.getMessage());
            return emptyList();
        }
    }

    private Sheets getSheets() {
        return new Sheets.Builder(credentialProvider.getHttpTransport(), JSON_FACTORY, credentialProvider.getCredentials())
                .setApplicationName(credentialProvider.getProperties().getAppName())
                .build();
    }
}
