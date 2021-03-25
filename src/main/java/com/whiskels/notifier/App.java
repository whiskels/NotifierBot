package com.whiskels.notifier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
@Slf4j
@Profile("!test")
public class App {
    public static void main(String[] args) {
        ApiContextInitializer.init();

        SpringApplication.run(App.class, args);
    }
}

