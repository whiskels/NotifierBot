package com.whiskels.notifier.telegram;

import com.whiskels.notifier.common.CreationEvent;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.MessageReceivedEvent;
import com.whiskels.notifier.telegram.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
@Profile("telegram-common")
class UpdateProcessor {
    private final HandlerOrchestrator orchestrator;
    private final UserService userService;

    @EventListener(MessageReceivedEvent.class)
    public void handleUpdate(CreationEvent<Update> updateCreationEvent) {
        Optional.ofNullable(MessageContext.from(updateCreationEvent.get()))
                .ifPresent(context -> {
                    User user = userService.getOrCreate(context.getUserId());
                    orchestrator.operate(user, context.getMessage());
                });
    }

    @Getter
    @AllArgsConstructor
    private static class MessageContext {
        private static final EnumSet<MessageContextType> CONTEXT_TYPES =
                EnumSet.allOf(MessageContextType.class);

        private final Long userId;
        private final String message;

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
            return new MessageContext(userId, text);
        }

        private static MessageContext fromCallBackQuery(Update update) {
            var msg = update.getCallbackQuery();
            Long userId = msg.getFrom().getId();
            String text = msg.getData();
            return new MessageContext(userId, text);
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
