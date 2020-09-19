package com.whiskels.telegrambot.bot;

import com.whiskels.telegrambot.model.Customer;
import com.whiskels.telegrambot.model.User;
import com.whiskels.telegrambot.service.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.whiskels.telegrambot.util.ThreadUtil.getThread;

/**
 * Main bot class
 *
 * @author whiskels
 */
@Component
@PropertySource("classpath:bot/bot.properties")
@Slf4j
public class Bot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    @Getter
    private String botUsername;

    @Value("${bot.token}")
    @Getter
    private String botToken;

    @Value("${bot.admin}")
    private String botAdmin;

    private final UpdateReceiver updateReceiver;

    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();

    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();

    private final Thread messageScheduler;
    private JSONReader jsonReader;

    @Getter
    private List<Customer> customerList;

    public Bot(UpdateReceiver updateReceiver,
               MessageScheduler messageScheduler,
               JSONReader jsonReader) {
        this.updateReceiver = updateReceiver;
        this.messageScheduler = getThread(messageScheduler, "MessageScheduler", 3);
        this.jsonReader = jsonReader;
    }

    @PostConstruct
    public void startBot() {
        messageScheduler.start();
        sendStartReport();
    }

    public void updateCustomers() {
        try {
            jsonReader.update();
            customerList = jsonReader.getCustomerList();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<PartialBotApiMethod<? extends Serializable>> messagesToSend = updateReceiver.handle(update);

        if (messagesToSend != null) {
            messagesToSend.forEach(response -> {
                if (response instanceof SendMessage) {
                    executeWithExceptionCheck((SendMessage) response);
                }
            });
        }
    }

    public void executeWithExceptionCheck(SendMessage sendMessage) {
        try {
            execute(sendMessage);
            log.debug("Executed {}", sendMessage);
        } catch (TelegramApiException e) {
            log.error("Exception while sending message to user: {}", e.getMessage());
        }
    }


    public void sendStartReport() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(botAdmin);
        sendMessage.setText("Bot start up is successful");
        executeWithExceptionCheck(sendMessage);
        log.info("Start report sent to Admin");
    }
}
