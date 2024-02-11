package com.whiskels.notifier.reporting.service.customer.birthday.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import static com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentReportConfig.PAYMENT_PROPERTIES_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = PAYMENT_PROPERTIES_PREFIX)
@Component
public class CustomerBirthdaySpreadsheetProperties {
    private String spreadsheet;
    private String cellRange;
}
