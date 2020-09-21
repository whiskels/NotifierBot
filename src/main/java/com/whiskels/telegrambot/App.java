package com.whiskels.telegrambot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApiContextInitializer.init();

        SpringApplication.run(App.class, args);
    }
}

