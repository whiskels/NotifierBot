package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.SendMessagePublisherTest;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import com.whiskels.notifier.telegram.security.SecuredAspect;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextConfiguration(classes = {SendMessageCreationEventPublisher.class, SecuredAspect.class})
public abstract class AbstractHandlerTest extends SendMessagePublisherTest {
    protected CommandHandler handler;

    protected void testInteraction(User user, String expectedAnswer) {
        assertNotNull(handler);
        handler.handle(user, "");

        verifyPublishedMessage(user.getChatId(), expectedAnswer);
    }

    protected void testUnauthorizedInteraction(User user, String message) {
        assertNotNull(handler);
        handler.handle(user, message);

        verifyUnauthorizedInteraction(user.getChatId(), message);
    }

    protected void testUnauthorizedInteraction(User user) {
        testUnauthorizedInteraction(user, "/foo");
    }

    protected void testInteraction(User user, String expectedAnswer, String expectedKeyboard) {
        assertNotNull(handler);
        handler.handle(user, "");

        SendMessage actual = verifyPublishedMessage(user.getChatId(), expectedAnswer);
        assertEquals(expectedKeyboard, actual.getReplyMarkup().toString());
    }
}
