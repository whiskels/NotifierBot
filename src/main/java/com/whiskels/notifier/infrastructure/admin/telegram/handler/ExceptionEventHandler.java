package com.whiskels.notifier.infrastructure.admin.telegram.handler;

import com.whiskels.notifier.infrastructure.admin.telegram.Bot;
import com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service.LogHandler;
import com.whiskels.notifier.infrastructure.admin.telegram.handler.retry.RetryHandler;
import com.whiskels.notifier.reporting.exception.ExceptionEvent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.button;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.createMarkup;
import static java.util.Objects.nonNull;

@Component
@Profile("telegram")
public class ExceptionEventHandler {
    private final String botAdmin;

    private final Bot bot;
    private final RetryHandler retryHandler;
    private final LogHandler logHandler;

    public ExceptionEventHandler(
            @Value("${telegram.bot.admin}") final String botAdmin,
            final Bot bot,
            final RetryHandler retryHandler,
            @Autowired(required = false) final LogHandler logHandler
    ) {
        this.botAdmin = botAdmin;
        this.bot = bot;
        this.retryHandler = retryHandler;
        this.logHandler = logHandler;
    }

    @EventListener(ExceptionEvent.class)
    @SneakyThrows
    public void handle(ExceptionEvent exceptionEvent) {
        var sendMessage = SendMessage.builder()
                .chatId(botAdmin)
                .text(exceptionEvent.message())
                .replyMarkup(createMarkup(getReplyMarkup(exceptionEvent.type().name())))
                .build();
        bot.execute(sendMessage);
    }

    private List<List<InlineKeyboardButton>> getReplyMarkup(String reportType) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(List.of(
                button(STR."\{retryHandler.getCommand().getDescription()}:\{reportType}",
                        STR."\{retryHandler.getCommand()} \{reportType}")
        ));
        if (nonNull(logHandler)) {
            keyboard.add(List.of(
                    button(logHandler.getCommand().getDescription(), logHandler.getCommand().name())
            ));
        }
        return keyboard;
    }
}
