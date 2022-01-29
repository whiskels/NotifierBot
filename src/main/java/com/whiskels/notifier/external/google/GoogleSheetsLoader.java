package com.whiskels.notifier.external.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.whiskels.notifier.external.Loader;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.Collections;
import java.util.List;

import static com.whiskels.notifier.external.google.GoogleCredentialProvider.JSON_FACTORY;

@Slf4j
@Lazy
@Component
@RequiredArgsConstructor
public abstract class GoogleSheetsLoader<T> implements Loader<T> {
    private final String spreadsheetId;
    private final String range;

    @Setter(onMethod = @__(@Autowired))
    private GoogleCredentialProvider credentialProvider;
    @Setter(onMethod = @__(@Autowired))
    protected Clock clock;

    protected abstract List<T> mapToData(List<List<Object>> values);

    @Override
    public List<T> load() {
        return mapToData(readSheet());
    }

    private List<List<Object>> readSheet() {
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
            log.error("No data found. while retrieving data from spreadsheet");
        }
        return values;
    }
}
