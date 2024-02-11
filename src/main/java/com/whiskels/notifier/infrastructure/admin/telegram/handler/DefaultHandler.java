package com.whiskels.notifier.infrastructure.admin.telegram.handler;

import com.whiskels.notifier.infrastructure.admin.telegram.BotMessage;
import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.CommandHandler;
import com.whiskels.notifier.infrastructure.admin.telegram.TextBotMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.whiskels.notifier.infrastructure.admin.telegram.Command.DEFAULT;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.button;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.createMarkup;
import static java.util.Collections.singleton;

@Primary
@Service
@RequiredArgsConstructor
class DefaultHandler implements CommandHandler {
    private final List<CommandHandler> handlers;

    @Override
    public BotMessage handle(String userId, String message) {
        var sendMessage = SendMessage.builder()
                .chatId(userId)
                .text("""
                        \uD83D\uDC4B Welcome to admin module
                        Here is what you can do
                        """)
                .replyMarkup(createMarkup(getKeyBoard()))
                .build();

        return TextBotMessage.of(sendMessage);
    }

    private List<List<InlineKeyboardButton>> getKeyBoard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        handlers.forEach(handler -> {
                    var command = handler.getCommand();
                    keyboard.add(List.of(button(command.getDescription(), command.name())));
                }
        );
        return keyboard;
    }

    @Override
    public Command getCommand() {
        return DEFAULT;
    }
}
