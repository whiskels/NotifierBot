package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.SendMessagePublisherTest;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextConfiguration(classes = {AuthorizationService.class})
public abstract class AbstractHandlerTest extends SendMessagePublisherTest {
    protected AbstractBaseHandler handler;

    protected void testInteraction(User user, String expectedAnswer) {
        assertNotNull(handler);
        handler.authorizeAndHandle(user, "");

        verifyPublishedMessage(user.getChatId(), expectedAnswer);
    }

    protected void testUnauthorizedInteraction(User user, String message) {
        assertNotNull(handler);
        handler.authorizeAndHandle(user, message);

        verifyUnauthorizedInteraction(user.getChatId(), message);
    }

    protected void testUnauthorizedInteraction(User user) {
        testUnauthorizedInteraction(user, "");
    }

    protected void testInteraction(User user, String expectedAnswer, String expectedKeyboard) {
        assertNotNull(handler);
        handler.authorizeAndHandle(user, "");

        SendMessage actual = verifyPublishedMessage(user.getChatId(), expectedAnswer);
        assertEquals(expectedKeyboard, actual.getReplyMarkup().toString());
    }
}
