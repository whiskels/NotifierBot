package com.whiskels.notifier.external.google.customer;

import com.whiskels.notifier.external.Loader;
import com.whiskels.notifier.external.MemoizingReportSupplier;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.google.GoogleSheetsReader;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

import static com.whiskels.notifier.external.google.customer._CustomerBirthdayInfoBeanConfig.PROPERTIES_PREFIX;

@Configuration
@ConditionalOnProperty(prefix = PROPERTIES_PREFIX, name = {"spreadsheet", "cell-range"})
@EnableConfigurationProperties(CustomerBirthdaySpreadsheetProperties.class)
class _CustomerBirthdayInfoBeanConfig {
    static final String PROPERTIES_PREFIX = "external.google.customer.birthday";

    @Bean
    public Loader<CustomerBirthdayInfo> customerBirthdayInfoLoader(
            Clock clock,
            GoogleSheetsReader spreadsheetLoader,
            CustomerBirthdaySpreadsheetProperties properties
    ) {
        return new CustomerBirthdayInfoLoader(clock, spreadsheetLoader, properties);
    }

    @Bean
    public ReportSupplier<CustomerBirthdayInfoDto> customerBirthdayInfoDtoSupplier(Clock clock, Loader<CustomerBirthdayInfo> loader) {
        ReportSupplier<CustomerBirthdayInfo> reportSupplier = new MemoizingReportSupplier<>(loader, clock);
        return () -> reportSupplier.get().remap(CustomerBirthdayInfoDto::from);
    }
}
