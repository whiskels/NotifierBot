package com.whiskels.notifier.external.google;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.whiskels.notifier.external.DataLoaderAndProvider;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static com.whiskels.notifier.external.google.GoogleCredentialProvider.JSON_FACTORY;

@Slf4j
@Service
@RequiredArgsConstructor
public abstract class InMemoryGoogleSheetsDataLoader<T> implements DataLoaderAndProvider<T> {
    @SuppressWarnings("unchecked")
    private final Class<T> genericClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), InMemoryGoogleSheetsDataLoader.class);
    private final String spreadsheetId;
    private final String range;

    @Setter(onMethod = @__(@Autowired))
    private GoogleCredentialProvider credentialProvider;
    @Setter(onMethod = @__(@Autowired))
    protected Clock clock;

    private List<T> data;
    private LocalDate lastUpdate;

    protected abstract List<T> mapToData(List<List<Object>> values);

    @PostConstruct
    @Override
    public List<T> update() {
        log.info("Updating {} list", genericClass.getSimpleName());
        try {
            data = mapToData(readSheet());
            log.info("Found {} {} data", data.size(), genericClass.getSimpleName());
            lastUpdate = LocalDate.now(clock);
        } catch (IOException e) {
            log.error("Exception while retrieving data from spreadsheet, abort updating");
        }
        return data;
    }

    private List<List<Object>> readSheet() throws IOException {
        Sheets service = new Sheets.Builder(
                credentialProvider.getHttpTransport(),
                JSON_FACTORY,
                credentialProvider.getCredentials()
        )
                .setApplicationName(credentialProvider.getAppName())
                .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            throw new IOException("No data found. while retrieving data from spreadsheet, abort updating");
        }
        return values;
    }

    @Override
    public List<T> getData() {
        return data;
    }

    @Override
    public LocalDate lastUpdate() {
        return lastUpdate;
    }
}
