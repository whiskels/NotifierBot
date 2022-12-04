package com.whiskels.notifier.external.json.operation;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalDate;

import static com.whiskels.notifier.common.util.DateTimeUtil.YEAR_MONTH_DAY_FORMATTER;
import static com.whiskels.notifier.common.util.DateTimeUtil.subtractWorkingDays;
import static com.whiskels.notifier.external.json.operation._FinOperationBeanConfig.FIN_OPERATION_URL;
import static com.whiskels.notifier.external.json.operation._FinOperationBeanConfig.PROPERTIES_PREFIX;
import static java.time.LocalDate.now;

class FinOperationRequestInterceptorConfig {
    @Value("${" + FIN_OPERATION_URL + "}")
    private String url;
    @Value("${" + PROPERTIES_PREFIX + ".working-days-to-load:2}")
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
