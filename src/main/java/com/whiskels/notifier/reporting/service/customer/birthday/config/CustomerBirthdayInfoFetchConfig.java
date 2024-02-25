
package com.whiskels.notifier.reporting.service.customer.birthday.config;

import com.whiskels.notifier.infrastructure.googlesheets.GoogleSheetsReader;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import com.whiskels.notifier.reporting.service.customer.birthday.fetch.CustomerBirthdayInfoFetchService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

import static com.whiskels.notifier.reporting.service.customer.birthday.config.CustomerBirthdayInfoFetchConfig.CUSTOMER_BIRTHDAY_PROPERTIES_PREFIX;

@Configuration
@ConditionalOnProperty(prefix = CUSTOMER_BIRTHDAY_PROPERTIES_PREFIX, name = {"spreadsheet", "cell-range"})
class CustomerBirthdayInfoFetchConfig {
    public static final String CUSTOMER_BIRTHDAY_PROPERTIES_PREFIX = "report.parameters.customer-birthday";

    @Bean
    @Primary
    DataFetchService<CustomerBirthdayInfo> customerBirthdayInfoDataFetchService(
            final Clock clock,
            final GoogleSheetsReader spreadsheetLoader,
            final CustomerBirthdaySpreadsheetProperties properties
    ) {
        return new CustomerBirthdayInfoFetchService(clock, spreadsheetLoader, properties.getSpreadsheet(), properties.getCellRange());
    }
}
