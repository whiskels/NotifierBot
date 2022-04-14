package com.whiskels.notifier.telegram;

import com.whiskels.notifier.PublisherTest;
import com.whiskels.notifier.telegram.event.SendMessageCreationEvent;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class SendMessagePublisherTest extends PublisherTest<SendMessageCreationEvent> {
    private static final String UNAUTH_MSG = format("Your token is *2*%nPlease contact your supervisor to gain access");
    protected static final String ADMIN_ID = "87971601";

    public final SendMessage verifyPublishedMessage(Long expectedUserId, String expectedMessage) {
        SendMessage actual = getPublishedObject().get();
        assertEquals(String.valueOf(expectedUserId), actual.getChatId());
        assertEquals(expectedMessage, actual.getText());
        return actual;
    }

    public final SendMessage getCapturedMessage() {
        return getPublishedObject().get();
    }

    public final void verifyUnauthorizedInteraction(Long userId, String message) {
        verify(publisher, times(2)).publishEvent(captor.capture());
        List<SendMessage> actualMessages = captor.getAllValues().stream()
                .map(SendMessageCreationEvent::get)
                .collect(Collectors.toList());
        assertEquals(String.valueOf(userId), actualMessages.get(0).getChatId());
        assertEquals(UNAUTH_MSG, actualMessages.get(0).getText());

        assertEquals(ADMIN_ID, actualMessages.get(1).getChatId());
        assertEquals(adminUnauthMessage(message), actualMessages.get(1).getText());
    }

    private static String adminUnauthMessage(String message) {
        return format("*Unauthorized access:*%nUser{chatId=2, name='Test user 2', roles=[UNAUTHORIZED]}, %s", message);
    }
}
