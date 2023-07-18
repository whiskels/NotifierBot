package com.whiskels.notifier;

import com.whiskels.notifier.slack.SlackConfig;
import com.whiskels.notifier.telegram.TelegramConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("!test")
@SpringBootApplication
@ComponentScan(basePackages = "com.whiskels.notifier",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"com.whiskels.notifier.telegram.*", "com.whiskels.notifier.slack.*"}))
@Import({TelegramConfig.class, SlackConfig.class})
@EnableScheduling
@EnableConfigurationProperties
@EnableFeignClients
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
