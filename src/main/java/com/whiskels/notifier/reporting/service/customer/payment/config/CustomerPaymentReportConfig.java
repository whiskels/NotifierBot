package com.whiskels.notifier.reporting.service.customer.payment.config;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.GenericReportService;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.payment.domain.CustomerPaymentDto;
import com.whiskels.notifier.reporting.service.customer.payment.messaging.PaymentReportMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import static com.whiskels.notifier.reporting.ReportType.CUSTOMER_PAYMENT;

@Configuration
@ConditionalOnBean(value = CustomerPaymentDto.class, parameterizedContainer = DataFetchService.class)
@EnableScheduling
public class CustomerPaymentReportConfig {
    public static final String PAYMENT_PROPERTIES_PREFIX = "report.parameters.customer-payment";

    @Bean
    ReportMessageConverter<CustomerPaymentDto> customerPaymentDtoReportMessageConverter(
            @Value("${report.parameters.customer-payment.header:\uD83D\uDCB8 Payment report on}") final String header,
            @Value("${report.parameters.customer-payment.no-data:Nothing}") final String noData,
            final CustomerPaymentReportPicProperties properties
    ) {
        return new PaymentReportMessageConverter(header, noData, properties.getPics());
    }

    @Bean
    GenericReportService<CustomerPaymentDto> customerOperationReportService(
            DataFetchService<CustomerPaymentDto> dataFetchService,
            ReportMessageConverter<CustomerPaymentDto> messageCreator
    ) {
        return new GenericReportService<>(CUSTOMER_PAYMENT, dataFetchService, messageCreator);
    }
}
