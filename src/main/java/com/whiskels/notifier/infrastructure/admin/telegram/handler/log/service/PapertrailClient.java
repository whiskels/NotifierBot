package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service;

import com.whiskels.notifier.infrastructure.admin.telegram.handler.log.domain.PaperTrailEventLog;
import com.whiskels.notifier.utilities.collections.StreamUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ConditionalOnProperty(PapertrailClient.PAPERTRAIL_API_TOKEN)
@FeignClient(name = "papertrailClient",
        url = "${papertrail.url:https://papertrailapp.com/api/v1/events/search.json}",
        configuration = PapertrailClient.PapertrailClientConfig.class)
public interface PapertrailClient {
    String PAPERTRAIL_HEADER = "X-Papertrail-Token";
    String PAPERTRAIL_API_TOKEN = "PAPERTRAIL_API_TOKEN";
    String PAPERTRAIL_API_TOKEN_SPEL = "${" + PAPERTRAIL_API_TOKEN + "}";

    @RequestMapping(method = RequestMethod.GET)
    PaperTrailEventLog getLog();

    default List<String> getLogs() {
        return Optional.ofNullable(getLog())
                .map(PaperTrailEventLog::getEvents)
                .map(events -> StreamUtil.map(events, PaperTrailEventLog.Event::getMessage))
                .orElse(Collections.emptyList());
    }

    record AuthRequestInterceptor(String token) implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            template.header(PAPERTRAIL_HEADER, token);
        }
    }

    class PapertrailClientConfig {
        @Bean
        AuthRequestInterceptor interceptor(@Value(PAPERTRAIL_API_TOKEN_SPEL) final String token) {
            return new AuthRequestInterceptor(token);
        }

        @Bean
        Retryer retryer(
                @Value("${papertrail.retry.period-millis:5000}") final Long periodMillis,
                @Value("${papertrail.retry.max-period-millis:15000}") final Long maxPeriodMillis,
                @Value("${papertrail.retry.max-attempts:3}") final int maxAttempts
        ) {
            return new Retryer.Default(periodMillis, maxPeriodMillis, maxAttempts);
        }
    }
}
