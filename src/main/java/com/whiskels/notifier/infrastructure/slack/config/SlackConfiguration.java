package com.whiskels.notifier.infrastructure.slack.config;

import com.slack.api.Slack;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class SlackConfiguration {
    @Bean
    Slack slack() {
        return Slack.getInstance();
    }
}
