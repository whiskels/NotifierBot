package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.events.SendMessageCreationEvent;
import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import com.whiskels.notifier.telegram.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static com.whiskels.notifier.telegram.UserTestData.*;
import static java.util.stream.Collectors.toList;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AdminMessageHandler.class})
@MockBean(UserService.class)
class AdminMessageHandlerTest extends AbstractHandlerTest {
    @Autowired
    private AdminMessageHandler adminMessageHandler;

    @Autowired
    private UserService userService;

    @Test
    void testAdminMessageHandler_Authorized() {
        List<User> actualUsers = List.of(USER_1, USER_2, USER_3);
        when(userService.getUsers()).thenReturn(actualUsers);

        adminMessageHandler.authorizeAndHandle(USER_1, "test_message");

        verify(userService).getUsers();
        verify(publisher, times(4)).publishEvent(captor.capture());

        List<SendMessage> actualMessages = captor.getAllValues().stream()
                .map(SendMessageCreationEvent::get)
                .collect(toList());

        assertEquals(4, actualMessages.size());
        assertTrue(actualMessages.stream()
                .filter(m -> m.getText()
                        .equalsIgnoreCase("test message\r\n"))
                .map(SendMessage::getChatId)
                .collect(toList())
                .containsAll(actualUsers.stream()
                        .map(User::getChatId)
                        .map(String::valueOf)
                        .collect(toList())));

        SendMessage adminMsg = actualMessages.get(3);
        assertEquals(String.valueOf(USER_1.getChatId()), adminMsg.getChatId());
        assertEquals("Notified 3 users\r\n", adminMsg.getText());

    }

    @Test
    void testAdminMessageHandler_Unauthorized() {
        handler = adminMessageHandler;
        testUnauthorizedInteraction(USER_2);
    }
}