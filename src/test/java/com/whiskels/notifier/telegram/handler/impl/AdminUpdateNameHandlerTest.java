package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import com.whiskels.notifier.telegram.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {AdminUpdateNameHandler.class})
@MockBean(UserService.class)
class AdminUpdateNameHandlerTest extends AbstractHandlerTest {
    @Autowired
    private AdminUpdateNameHandler adminMessageHandler;

    @Autowired
    private UserService userService;

    @Test
    void testAdminUpdateNameHandler_AuthorizedAndUserFound() {
        User updated = USER_2;
        updated.setName("updated");
        when(userService.get(USER_2.getChatId())).thenReturn(Optional.of(USER_2));
        when(userService.update(updated)).thenReturn(updated);

        adminMessageHandler.handle(USER_1, "/admin_update 2 updated");
        verify(userService).get(USER_2.getChatId());
        verify(userService).update(updated);
        verify(publisher).publishEvent(captor.capture());

        SendMessage actual = getCapturedMessage();
        assertEquals(String.valueOf(USER_1.getChatId()), actual.getChatId());
        assertEquals(format("Updated user: User{chatId=2, name='updated', roles=[UNAUTHORIZED]}%n"), actual.getText());
    }

    @Test
    void testAdminUpdateNameHandler_AuthorizedAndUserNotFound() {
        when(userService.get(4)).thenReturn(Optional.empty());

        adminMessageHandler.handle(USER_1, "/admin_update 4 updated");
        verify(userService).get(4);
        verify(publisher).publishEvent(captor.capture());

        SendMessage actual = getCapturedMessage();
        assertEquals(String.valueOf(USER_1.getChatId()), actual.getChatId());
        assertEquals(format("Couldn't find user: 4%n"), actual.getText());
    }

    @Test
    void testAdminUpdateNameHandler_Unauthorized() {
        handler = adminMessageHandler;
        testUnauthorizedInteraction(USER_2);
    }
}
