package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.external.google.InMemoryGoogleSheetsDataLoader;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.*;
import static java.time.LocalDate.now;

@Service
@ConditionalOnProperty("external.google.customer.birthday.spreadsheet")
public class CustomerBirthdayInfoDataLoader extends InMemoryGoogleSheetsDataLoader<CustomerBirthdayInfo> implements TelegramLabeled {
    @Value("${external.google.customer.telegram.label:Customer birthdays}")
    private String telegramLabel;
    @Value("${external.google.customer.column.company:1}")
    private int companyColumnIndex;
    @Value("${external.google.customer.column.name:2}")
    private int nameColumnIndex;
    @Value("${external.google.customer.column.birthday:4}")
    private int birthdayColumnIndex;

    public CustomerBirthdayInfoDataLoader(
            @Value("${external.google.customer.birthday.spreadsheet}") String spreadsheetId,
            @Value("${external.google.customer.birthday.cell.range}") String range) {
        super(spreadsheetId, range);
    }

    @Scheduled(cron = "${external.google.customer.birthday.cron:0 30 8 * * MON-FRI}", zone = "${common.timezone}")
    public void updateScheduled() {
        update();
    }

    @Override
    protected List<CustomerBirthdayInfo> mapToData(List<List<Object>> values) {
        return values.stream()
                .map(value -> {
                    if (value.size() < maxColumnIndex()) {
                        return null;
                    }
                    return CustomerBirthdayInfo.builder()
                            .companyName((String) value.get(companyColumnIndex))
                            .name((String) value.get(nameColumnIndex))
                            .birthday(parseDate((String) value.get(birthdayColumnIndex)))
                            .build();
                })
                .filter(customerBirthdayInfo -> customerBirthdayInfo != null && isSameMonth(customerBirthdayInfo.getBirthday(), now(clock)))
                .sorted(birthdayComparator())
                .collect(Collectors.toList());
    }

    private int maxColumnIndex() {
        return Math.max(companyColumnIndex, Math.max(nameColumnIndex, birthdayColumnIndex) + 1);
    }

    @Override
    public String getLabel() {
        return telegramLabel;
    }
}
