package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.infrastructure.config.feign.FeignProxyConfig;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentFetchConfig.PAYMENT_URL;
import static com.whiskels.notifier.reporting.service.customer.payment.config.CustomerPaymentReportConfig.PAYMENT_PROPERTIES_PREFIX;
import static com.whiskels.notifier.utilities.DateTimeUtil.subtractWorkingDays;
import static java.time.LocalDate.now;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@ConditionalOnProperty(PAYMENT_URL)
@FeignClient(name = "finOperationClient",
        configuration = FinOperationFeignClient.FinOperationRequestInterceptorConfig.class,
        url = "https://")
public interface FinOperationFeignClient {
    @RequestMapping(method = GET)
    List<FinancialOperation> get();

    @Import(FeignProxyConfig.class)
    class FinOperationRequestInterceptorConfig {
        private static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        @Value("${" + PAYMENT_URL + "}")
        private String url;
        @Value("${" + PAYMENT_PROPERTIES_PREFIX + ".working-days-to-load:2}")
        private int workingDaysToLoad;

        @Bean
        public RequestInterceptor urlPreparingInterceptor(Clock clock) {
            return requestTemplate -> {
                LocalDate endDate = now(clock).minusDays(1);
                LocalDate startDate = subtractWorkingDays(endDate, workingDaysToLoad);
                var finalUrl = url
                        .replace("startDate", startDate.format(YEAR_MONTH_DAY_FORMATTER))
                        .replace("endDate", endDate.format(YEAR_MONTH_DAY_FORMATTER));
                requestTemplate.target(finalUrl);
            };
        }
    }
}
