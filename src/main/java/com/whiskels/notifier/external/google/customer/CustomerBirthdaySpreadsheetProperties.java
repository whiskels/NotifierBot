package com.whiskels.notifier.external.google.customer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import static com.whiskels.notifier.external.google.customer._CustomerBirthdayInfoBeanConfig.PROPERTIES_PREFIX;

@Getter
@Setter
@ConfigurationProperties(prefix = PROPERTIES_PREFIX)
class CustomerBirthdaySpreadsheetProperties {
    private String spreadsheet;
    private String cellRange;
}
