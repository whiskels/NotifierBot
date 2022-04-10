package com.whiskels.notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@Profile("!test")
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
@EnableRetry
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
