package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {AdminTimeHandler.class, MockedClockConfiguration.class})
class AdminTimeHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_BOT_TIME_FORMATTED = "2014-12-22 10:15:30";

    @Autowired
    private AdminTimeHandler adminTimeHandler;

    @BeforeEach
    public void setHandler() {
        handler = adminTimeHandler;
    }

    @Test
    void testAdminTimeHandler_Authorized() {
        adminTimeHandler.authorizeAndHandle(USER_1, null);

        verify(publisher).publishEvent(captor.capture());
        SendMessage actual = getCapturedMessage();
        assertEquals(String.valueOf(USER_1.getChatId()), actual.getChatId());
        assertTrue(actual.getText().contains(EXPECTED_BOT_TIME_FORMATTED));
    }

    @Test
    void testAdminTimeHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2);
    }
}
