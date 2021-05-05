package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.events.SendMessageCreationEvent;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(AbstractHandlerTest.PublisherConfig.class)
@ContextConfiguration(classes = {AuthorizationService.class})
public abstract class AbstractHandlerTest {
    private static final String UNAUTH_MSG = format("Your token is *2*%nPlease contact your supervisor to gain access%n");
    private static final String ADMIN_MSG = format("*Unauthorized access:* 2%n*Message:* Empty%n");
    protected static final String ADMIN_ID = "87971601";

    @Autowired
    protected ApplicationEventPublisher publisher;

    @Captor
    protected ArgumentCaptor<SendMessageCreationEvent> captor;

    protected AbstractBaseHandler handler;


    @TestConfiguration
    static class PublisherConfig {
        @Bean
        @Primary
        GenericApplicationContext genericApplicationContext(final GenericApplicationContext gac) {
            return Mockito.spy(gac);
        }
    }

    protected void testInteraction(User user, String expectedAnswer) {
        assertNotNull(handler);
        handler.authorizeAndHandle(user, "");

        verify(publisher).publishEvent(captor.capture());
        SendMessage actual = getCapturedMessage();
        assertEquals(String.valueOf(user.getChatId()), actual.getChatId());
        assertEquals(expectedAnswer, actual.getText());
    }

    protected void testUnauthorizedInteraction(User user) {
        assertNotNull(handler);
        handler.authorizeAndHandle(user, "");

        verify(publisher, times(2)).publishEvent(captor.capture());
        List<SendMessage> actualMessages = captor.getAllValues().stream()
                .map(SendMessageCreationEvent::get)
                .collect(Collectors.toList());
        assertEquals(String.valueOf(user.getChatId()), actualMessages.get(0).getChatId());
        assertEquals(UNAUTH_MSG, actualMessages.get(0).getText());

        assertEquals(ADMIN_ID, actualMessages.get(1).getChatId());
        assertEquals(ADMIN_MSG, actualMessages.get(1).getText());
    }

    protected void testInteraction(User user, String expectedAnswer, String expectedKeyboard) {
        assertNotNull(handler);
        handler.authorizeAndHandle(user, "");

        verify(publisher).publishEvent(captor.capture());
        SendMessage actual = getCapturedMessage();
        assertEquals(String.valueOf(user.getChatId()), actual.getChatId());
        assertEquals(expectedAnswer, actual.getText());
        assertEquals(expectedKeyboard, actual.getReplyMarkup().toString());
    }

    protected final SendMessage getCapturedMessage() {
        return captor.getValue().get();
    }
}
