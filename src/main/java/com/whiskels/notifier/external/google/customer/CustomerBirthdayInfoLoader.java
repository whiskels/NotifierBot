package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.google.GoogleSheetsReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.datetime.DateTimeUtil.isSameMonth;
import static java.time.LocalDate.now;

@Component
@ConditionalOnProperty("external.google.customer.birthday.spreadsheet")
@RequiredArgsConstructor
public class CustomerBirthdayInfoLoader implements Loader<CustomerBirthdayInfo> {
    private final Clock clock;
    private final GoogleSheetsReader spreadsheetLoader;

    @Value("${external.google.customer.birthday.spreadsheet}")
    private String spreadsheetId;
    @Value("${external.google.customer.birthday.cell-range}")
    private String range;

    @Override
    public List<CustomerBirthdayInfo> load() {
       return spreadsheetLoader.read(spreadsheetId, range).stream()
               .map(CustomerBirthdayInfo::fromExcelData)
               .filter(customerBirthdayInfo -> customerBirthdayInfo != null && isSameMonth(customerBirthdayInfo.getBirthday(), now(clock)))
               .sorted(birthdayComparator())
               .collect(Collectors.toList());
    }
}
