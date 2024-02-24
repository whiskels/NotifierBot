package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.config;

import com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service.LogHandler;
import com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service.LogService;
import com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service.PapertrailClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(PapertrailClient.PAPERTRAIL_API_TOKEN)
@Profile("telegram")
class LogHandlerConfiguration {
    @Bean
    LogService logService(final PapertrailClient papertrailClient) {
        return new LogService(papertrailClient);
    }

    @Bean
    LogHandler logHandler(final LogService logService) {
        return new LogHandler(logService);
    }
}
