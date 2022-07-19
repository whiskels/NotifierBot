package com.whiskels.notifier.external.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static com.whiskels.notifier.external.google.GoogleCredentialProvider.JSON_FACTORY;

@Slf4j
@Lazy
@Component
@RequiredArgsConstructor
public class GoogleSheetsReader {
    private final GoogleCredentialProvider credentialProvider;

    public List<List<Object>> read(String spreadsheetId, String range) {
        Sheets service = new Sheets.Builder(
                credentialProvider.getHttpTransport(),
                JSON_FACTORY,
                credentialProvider.getCredentials()
        )
                .setApplicationName(credentialProvider.getAppName())
                .build();
        List<List<Object>> values = Collections.emptyList();
        try {
            ValueRange response = service.spreadsheets().values()
                    .get(spreadsheetId, range)
                    .execute();
            values = response.getValues();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (values == null || values.isEmpty()) {
            log.error("No data found while retrieving data from spreadsheet");
        }
        return values;
    }
}
