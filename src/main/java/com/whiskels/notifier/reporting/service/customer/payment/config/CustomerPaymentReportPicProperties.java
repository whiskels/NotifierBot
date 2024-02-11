package com.whiskels.notifier.reporting.service.customer.payment.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;

import static com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentReportConfig.PAYMENT_PROPERTIES_PREFIX;

@ConfigurationProperties(PAYMENT_PROPERTIES_PREFIX)
@Getter
@Setter
class CustomerPaymentReportPicProperties {
    private Map<Integer, List<String>> pics;
}
