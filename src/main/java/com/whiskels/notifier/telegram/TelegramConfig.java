package com.whiskels.notifier.telegram;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(basePackages = "com.whiskels.notifier.telegram")
@Profile("telegram-common")
public class TelegramConfig {
}
