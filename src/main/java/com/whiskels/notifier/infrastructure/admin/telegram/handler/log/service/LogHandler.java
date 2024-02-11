package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.service;

import com.whiskels.notifier.infrastructure.admin.telegram.BotMessage;
import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.CommandHandler;

import com.whiskels.notifier.infrastructure.admin.telegram.DocumentBotMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;

import static com.whiskels.notifier.infrastructure.admin.telegram.Command.GET_LOGS;

@RequiredArgsConstructor
public class LogHandler implements CommandHandler {
    private static final String MESSAGE = "Application logs";
    private static final String FILE_NAME = "logs.txt";
    private final LogService logService;

    @Override
    public BotMessage handle(final String userId, final String message) {
        var document = SendDocument.builder()
                .chatId(userId)
                .caption(MESSAGE)
                .document(getFile())
                .build();
        return DocumentBotMessage.of(document);
    }

    private InputFile getFile() {
        ByteArrayInputStream bais = new ByteArrayInputStream(logService.getLogsAsByteArray());
        return new InputFile(bais, FILE_NAME);
    }

    @Override
    public Command getCommand() {
        return GET_LOGS;
    }
}
