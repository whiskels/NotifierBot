package com.whiskels.notifier.infrastructure.admin.telegram.handler.retry;

import com.whiskels.notifier.infrastructure.admin.telegram.Bot;
import com.whiskels.notifier.infrastructure.admin.telegram.BotMessage;
import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.CommandHandler;
import com.whiskels.notifier.infrastructure.admin.telegram.TextBotMessage;
import com.whiskels.notifier.reporting.service.ReportServiceImpl;
import com.whiskels.notifier.reporting.ReportType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.whiskels.notifier.infrastructure.admin.telegram.Command.RETRY_REPORT;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.button;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.createMarkup;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.extractArguments;
import static java.util.Collections.singleton;

@Service
@RequiredArgsConstructor
@Profile("telegram")
public class RetryHandler implements CommandHandler {
    private final ReportServiceImpl reportService;

    @Override
    public BotMessage handle(final String userId, final String message) {
        if (!message.contains(" ")) {
            return menuMessage(userId);
        } else {
            return retryReport(userId, extractArguments(message));
        }
    }

    private BotMessage menuMessage(final String userId) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        var reportTypes = reportService.getReportTypes();
        reportTypes.forEach(type ->
                keyboard.add(List.of(button(type.name(), STR."\{getCommand()} \{type.name()}")))
        );

        var message = SendMessage.builder()
                .chatId(userId)
                .text("Choose report to retry")
                .replyMarkup(createMarkup(keyboard))
                .build();

        return TextBotMessage.of(message);
    }

    private BotMessage retryReport(final String userId, final String argument) {
        ReportType type = ReportType.valueOf(argument);
        reportService.executeReport(type);
        var message = SendMessage.builder()
                .chatId(userId)
                .text(STR."Retried \{type.name()}")
                .build();
        return TextBotMessage.of(message);
    }

    @Override
    public Command getCommand() {
        return RETRY_REPORT;
    }
}
