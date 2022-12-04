package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.audit.Audit;
import com.whiskels.notifier.external.google.GoogleSheetsReader;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.util.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.util.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.external.LoaderType.CUSTOMER_BIRTHDAY;
import static java.time.LocalDate.now;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
class CustomerBirthdayInfoLoader implements Loader<CustomerBirthdayInfo> {
    private final Clock clock;
    private final GoogleSheetsReader spreadsheetLoader;
    private final CustomerBirthdaySpreadsheetProperties properties;

    @Override
    @Audit(loader = CUSTOMER_BIRTHDAY)
    public List<CustomerBirthdayInfo> load() {
       return spreadsheetLoader.read(properties.getSpreadsheet(), properties.getCellRange()).stream()
               .map(CustomerBirthdayInfo::fromExcelData)
               .filter(customerBirthdayInfo -> nonNull(customerBirthdayInfo) && isSameMonth(customerBirthdayInfo.getBirthday(), now(clock)))
               .sorted(birthdayComparator())
               .collect(Collectors.toList());
    }
}
