package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.external.google.GoogleSheetsLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.*;
import static java.time.LocalDate.now;

@Component
@ConditionalOnProperty("external.google.customer.birthday.spreadsheet")
public class CustomerBirthdayInfoLoader extends GoogleSheetsLoader<CustomerBirthdayInfo> {
    private final Predicate<CustomerBirthdayInfo> customerBirthdayInfoPredicate =
            customerBirthdayInfo -> customerBirthdayInfo != null && isSameMonth(customerBirthdayInfo.getBirthday(), now(clock));

    @Value("${external.google.customer.column.company:1}")
    private int companyColumnIndex;
    @Value("${external.google.customer.column.name:2}")
    private int nameColumnIndex;
    @Value("${external.google.customer.column.birthday:4}")
    private int birthdayColumnIndex;

    public CustomerBirthdayInfoLoader(
            @Value("${external.google.customer.birthday.spreadsheet}") String spreadsheetId,
            @Value("${external.google.customer.birthday.cell.range}") String range) {
        super(spreadsheetId, range);
    }

    @Override
    protected List<CustomerBirthdayInfo> mapToData(List<List<Object>> values) {
        return values.stream()
                .map(this::mapToCustomerBirthdayInfo)
                .filter(customerBirthdayInfoPredicate)
                .sorted(birthdayComparator())
                .collect(Collectors.toList());
    }

    private CustomerBirthdayInfo mapToCustomerBirthdayInfo(List<Object> data) {
        if (data.size() < maxColumnIndex()) {
            return null;
        }
        return CustomerBirthdayInfo.builder()
                .companyName((String) data.get(companyColumnIndex))
                .name((String) data.get(nameColumnIndex))
                .birthday(parseDate((String) data.get(birthdayColumnIndex)))
                .build();
    }

    private int maxColumnIndex() {
        return Math.max(companyColumnIndex, Math.max(nameColumnIndex, birthdayColumnIndex) + 1);
    }
}
