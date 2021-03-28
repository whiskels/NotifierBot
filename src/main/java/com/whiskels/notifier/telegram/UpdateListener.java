package com.whiskels.notifier.telegram;

import com.whiskels.notifier.common.CreationEvent;
import com.whiskels.notifier.telegram.events.UpdateCreationEvent;
import com.whiskels.notifier.telegram.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Main class used to handle incoming Updates.
 * Chooses suitable inheritor of AbstractBaseHandler to handle the input
 */
@Component
@Slf4j
@RequiredArgsConstructor
@Profile("telegram-common")
public class UpdateListener {
    private final HandlerProvider handlerProvider;
    private final UserService userService;

    @EventListener(classes = {UpdateCreationEvent.class})
    public void handleUpdate(CreationEvent<Update> updateCreationEvent) {
        final Update update = updateCreationEvent.get();
        try {
            int userId = 0;
            String text = null;

            if (isMessageWithText(update)) {
                final Message message = update.getMessage();
                userId = message.getFrom().getId();
                text = message.getText();
                log.debug("Update is text message {} from {}", text, userId);
            } else if (update.hasCallbackQuery()) {
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                userId = callbackQuery.getFrom().getId();
                text = callbackQuery.getData();
                log.debug("Update is callbackquery {} from {}", text, userId);
            }

            if (text != null && userId != 0) {
                handlerProvider.getHandler(text)
                        .authorizeAndHandle(userService.getOrCreate(userId), text);
            } else {
                throw new UnsupportedOperationException();
            }
        } catch (UnsupportedOperationException e) {
            log.debug("Command: {} is unsupported", update.toString());
        }
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }
}
