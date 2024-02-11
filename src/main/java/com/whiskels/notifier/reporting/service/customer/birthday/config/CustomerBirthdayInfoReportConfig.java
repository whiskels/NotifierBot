package com.whiskels.notifier.reporting.service.customer.birthday.config;

import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.GenericReportService;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.birthday.convert.ReportContext;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import com.whiskels.notifier.reporting.service.customer.birthday.convert.CustomerBirthdayInfoReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.birthday.convert.context.BeforeEventReportContext;
import com.whiskels.notifier.reporting.service.customer.birthday.convert.context.DailyReportContext;
import com.whiskels.notifier.reporting.service.customer.birthday.convert.context.MonthMiddleReportContext;
import com.whiskels.notifier.reporting.service.customer.birthday.convert.context.MonthStartReportContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@Configuration
@ConditionalOnBean(value = CustomerBirthdayInfo.class, parameterizedContainer = DataFetchService.class)
class CustomerBirthdayInfoReportConfig {

    @Bean
    GenericReportService<CustomerBirthdayInfo> customerBirthdayInfoReportService(
            final DataFetchService<CustomerBirthdayInfo> dataFetchService,
            final ReportMessageConverter<CustomerBirthdayInfo> messageConverter
    ) {
        return new GenericReportService<>(ReportType.CUSTOMER_BIRTHDAY, dataFetchService, messageConverter);
    }

    @Bean
    ReportMessageConverter<CustomerBirthdayInfo> customerBirthdayInfoReportMessageConverter(
            List<ReportContext> contexts,
            @Value("${report.parameters.customer-birthday.no-data:Nobody}") final String noData) {

        return new CustomerBirthdayInfoReportMessageConverter(contexts, noData);
    }

    @Bean
    ReportContext dailyReportCustomerContext(
            @Value("${report.parameters.customer-birthday.daily.header:\uD83C\uDF89 Customer events on}") final String headerPrefix) {
        return new DailyReportContext(headerPrefix);
    }

    @Bean
    ReportContext monthStartReportCustomerContext(
            @Value("${report.parameters.customer-birthday.month-start.header:\uD83D\uDDD3\uFE0F Upcoming customer events this month}") final String headerPrefix) {
        return new MonthStartReportContext(headerPrefix);
    }

    @Bean
    ReportContext monthMiddleReportCustomerContext(
            @Value("${report.parameters.customer-birthday.month-middle.header:\uD83D\uDDD3\uFE0F Upcoming customer events till end of month}") final String headerPrefix) {
        return new MonthMiddleReportContext(headerPrefix);
    }

    @Bean
    ReportContext daysBeforeReportCustomerContext(
            @Value("${report.parameters.customer-birthday.before.header:⏰ Upcoming customer events in}") final String headerPrefix,
            @Value("${report.parameters.customer-birthday.before.days:7}") final int daysBefore) {
        return new BeforeEventReportContext(STR."\{headerPrefix} \{daysBefore} from", daysBefore);
    }
}
