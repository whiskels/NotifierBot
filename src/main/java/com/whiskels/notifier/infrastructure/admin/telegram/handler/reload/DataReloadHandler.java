package com.whiskels.notifier.infrastructure.admin.telegram.handler.reload;

import com.whiskels.notifier.infrastructure.admin.telegram.BotMessage;
import com.whiskels.notifier.infrastructure.admin.telegram.Command;
import com.whiskels.notifier.infrastructure.admin.telegram.CommandHandler;
import com.whiskels.notifier.infrastructure.admin.telegram.TextBotMessage;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.audit.LoadAuditRepository;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.button;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.createMarkup;
import static com.whiskels.notifier.infrastructure.admin.telegram.util.TelegramUtil.extractArguments;
import static com.whiskels.notifier.utilities.formatters.StringFormatter.COLLECTOR_NEW_LINE;
import static java.util.Collections.singleton;

@Service
@Profile("telegram")
public class DataReloadHandler implements CommandHandler {
    private final Map<String, DataFetchService<?>> dataFetchServiceMap;
    private final LoadAuditRepository loadAuditRepository;

    public DataReloadHandler(final List<DataFetchService<?>> dataFetchServices,
                             final LoadAuditRepository loadAuditRepository
    ) {
        this.dataFetchServiceMap = dataFetchServices.stream()
                .collect(Collectors.toMap(service -> AopUtils.getTargetClass(service).getSimpleName(), Function.identity()));
        this.loadAuditRepository = loadAuditRepository;
    }

    @Override
    public BotMessage handle(final String chatId, final String message) {
        if (!message.contains(" ")) {
            return menuMessage(chatId);
        } else {
            return reloadMessage(chatId, extractArguments(message));
        }
    }

    private BotMessage menuMessage(final String userId) {
        var message = SendMessage.builder()
                .chatId(userId)
                .text(String.format(STR."""
                        Last data load results:
                        \{getLastUpdates()}
                        Choose class to reload
                        """))
                .replyMarkup(createMarkup(getMenuKeyboard()))
                .build();
        return TextBotMessage.of(message);
    }

    private BotMessage reloadMessage(String userId, String argument) {
        var data = dataFetchServiceMap.get(argument).fetch();
        var message = SendMessage.builder()
                .chatId(userId)
                .text(STR."Data reloaded: \{data.data().size()}")
                .build();
        return TextBotMessage.of(message);
    }

    private List<List<InlineKeyboardButton>> getMenuKeyboard() {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        dataFetchServiceMap.keySet().forEach(name ->
                keyboard.add(List.of(button(name, STR."\{getCommand()} \{name}")))
        );
        return keyboard;
    }

    private String getLastUpdates() {
        return loadAuditRepository.getLast(PageRequest.of(0, 10))
                .stream()
                .map(loadAudit -> STR."\{loadAudit.getReportType()}: \{loadAudit.getCount()}")
                .collect(COLLECTOR_NEW_LINE);
    }

    @Override
    public Command getCommand() {
        return Command.RELOAD_DATA;
    }
}
