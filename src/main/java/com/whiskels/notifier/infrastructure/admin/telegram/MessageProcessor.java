package com.whiskels.notifier.infrastructure.admin.telegram;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Slf4j
@Component
@Profile("telegram")
class MessageProcessor {
    private final String botAdmin;
    private final Map<Command, CommandHandler> handlers;
    private final CommandHandler defaultHandler;

    public MessageProcessor(@Value("${telegram.bot.admin}") String botAdmin,
                            List<CommandHandler> handlers,
                            CommandHandler defaultHandler
    ) {
        this.botAdmin = botAdmin;
        this.handlers = handlers.stream().collect(Collectors.toMap(CommandHandler::getCommand, Function.identity()));
        this.defaultHandler = defaultHandler;
    }

    @SneakyThrows
    @Nullable
    public BotMessage onUpdateReceived(Update update) {
        var context = MessageContext.from(update);

        if (isNull(context)) {
            log.warn("Unrecognized command: {}", update);
            return null;
        }
        if (!Objects.equals(context.userId, botAdmin)) {
            log.error(STR."Unauthorized access attempt: \{context.userId}");
            return null;
        }

        var message = context.message;
        return handlers.getOrDefault(extractCommand(message), defaultHandler)
                .handle(context.userId, message);
    }

    private static Command extractCommand(String message) {
        String textCommand = message.split(" ")[0];
        try {
            return Command.valueOf(textCommand);
        } catch (Exception e) {
            log.warn("Unrecognized command: {}", textCommand);
            return Command.DEFAULT;
        }
    }

    private record MessageContext(String userId, String message) {
        private static final EnumSet<MessageContextType> CONTEXT_TYPES =
                EnumSet.allOf(MessageContextType.class);

        @Nullable
        public static MessageContext from(Update update) {
            return CONTEXT_TYPES.stream()
                    .filter(type -> type.isApplicable(update))
                    .findFirst()
                    .map(type -> type.extractContext(update))
                    .orElse(null);
        }

        private static MessageContext fromMessage(Update update) {
            var msg = update.getMessage();
            Long userId = msg.getFrom().getId();
            String text = msg.getText();
            return new MessageContext(String.valueOf(userId), text);
        }

        private static MessageContext fromCallBackQuery(Update update) {
            var msg = update.getCallbackQuery();
            Long userId = msg.getFrom().getId();
            String text = msg.getData();
            return new MessageContext(String.valueOf(userId), text);
        }
    }

    @RequiredArgsConstructor
    private enum MessageContextType {
        CALLBACK_QUERY(Update::hasCallbackQuery, MessageContext::fromCallBackQuery),
        TEXT(MessageContextType::isMessageWithText, MessageContext::fromMessage);

        private final Predicate<Update> checker;
        private final Function<Update, MessageContext> converter;

        static boolean isMessageWithText(Update update) {
            return update.hasMessage() && update.getMessage().hasText();
        }

        private boolean isApplicable(Update update) {
            return checker.test(update);
        }

        private MessageContext extractContext(Update update) {
            return converter.apply(update);
        }
    }
}
