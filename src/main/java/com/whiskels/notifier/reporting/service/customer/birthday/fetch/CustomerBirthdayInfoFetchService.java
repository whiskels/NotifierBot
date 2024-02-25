package com.whiskels.notifier.reporting.service.customer.birthday.fetch;

import com.whiskels.notifier.infrastructure.googlesheets.GoogleSheetsReader;
import com.whiskels.notifier.reporting.domain.HasBirthday;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.audit.AuditDataFetchResult;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.time.Clock;
import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_BIRTHDAY;
import static com.whiskels.notifier.utilities.DateTimeUtil.isSameMonth;
import static com.whiskels.notifier.utilities.DateTimeUtil.parseDate;
import static java.time.LocalDate.now;
import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class CustomerBirthdayInfoFetchService implements DataFetchService<CustomerBirthdayInfo> {
    private final Clock clock;
    private final GoogleSheetsReader spreadsheetLoader;
    private final String spreadSheet;
    private final String cellRange;

    @Nonnull
    @AuditDataFetchResult(reportType = CUSTOMER_BIRTHDAY)
    @Override
    public ReportData<CustomerBirthdayInfo> fetch() {
        return new ReportData<>(load(), now(clock));
    }

    private List<CustomerBirthdayInfo> load() {
        return spreadsheetLoader.read(spreadSheet, cellRange).stream()
                .map(CustomerBirthdayInfoFetchService::mapFromExcelData)
                .filter(customerBirthdayInfo -> nonNull(customerBirthdayInfo) && isSameMonth(customerBirthdayInfo.birthday(), now(clock)))
                .sorted(HasBirthday.comparator())
                .collect(Collectors.toList());
    }

    //TODO search for a better way of excel parsing - maybe query?
    private static CustomerBirthdayInfo mapFromExcelData(List<Object> data) {
        if (data.size() < 11) return null;
        try {
            return CustomerBirthdayInfo.builder()
                    .responsible((String) data.get(0))
                    .responsibleEmail((String) data.get(1))
                    .clientId((String) data.get(2))
                    .company((String) data.get(3))
                    .name((String) data.get(4))
                    .surname((String) data.get(5))
                    .email((String) data.get(6))
                    .telegram((String) data.get(7))
                    .phone((String) data.get(8))
                    .birthday(parseDate((String) data.get(9)))
                    .position((String) data.get(10))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
