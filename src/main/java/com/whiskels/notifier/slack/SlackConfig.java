package com.whiskels.notifier.slack;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ComponentScan(basePackages = "com.whiskels.notifier.slack")
@Profile("slack-common")
public class SlackConfig {
}
