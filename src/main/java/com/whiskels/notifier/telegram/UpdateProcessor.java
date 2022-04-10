package com.whiskels.notifier.telegram;

import com.whiskels.notifier.common.CreationEvent;
import com.whiskels.notifier.telegram.event.UpdateCreationEvent;
import com.whiskels.notifier.telegram.orchestrator.HandlerOrchestrator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Main class used to handle incoming Updates.
 * Verifies incoming update and delegates handling to {@link HandlerOrchestrator}
 */
@Component
@RequiredArgsConstructor
@Profile("telegram-common")
public class UpdateProcessor {
    private final HandlerOrchestrator orchestrator;

    @EventListener(UpdateCreationEvent.class)
    public void handleUpdate(CreationEvent<Update> updateCreationEvent) {
        final Update update = updateCreationEvent.get();

        MessageContext context = null;
        if (isMessageWithText(update)) {
            context = MessageContext.fromMessage(update);
        } else if (update.hasCallbackQuery()) {
            context = MessageContext.fromCallBackQuery(update);
        }

        if (context != null) {
            orchestrator.operate(context.getUserId(), context.getMessage());
        }
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    @AllArgsConstructor
    @Getter
    static class MessageContext {
        private final int userId;
        private final String message;

        static MessageContext fromMessage(Update update) {
            var msg = update.getMessage();
            int userId = msg.getFrom().getId();
            String text = msg.getText();
            return new MessageContext(userId, text);
        }

        static MessageContext fromCallBackQuery(Update update) {
            var msg = update.getCallbackQuery();
            int userId = msg.getFrom().getId();
            String text = msg.getData();
            return new MessageContext(userId, text);
        }
    }
}
