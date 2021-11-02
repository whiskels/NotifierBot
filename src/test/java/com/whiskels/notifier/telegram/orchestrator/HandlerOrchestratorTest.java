package com.whiskels.notifier.telegram.orchestrator;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.telegram.SendMessagePublisherTest;
import com.whiskels.notifier.telegram.handler.impl.AdminTimeHandler;
import com.whiskels.notifier.telegram.handler.impl.HelpHandler;
import com.whiskels.notifier.telegram.handler.impl.TokenHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import com.whiskels.notifier.telegram.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static com.whiskels.notifier.telegram.Command.*;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {HandlerOrchestrator.class, AuthorizationService.class,
        TokenHandler.class, HelpHandler.class, AdminTimeHandler.class
})
@Import({HandlerOrchestratorTest.UserServiceBeanConfig.class, MockedClockConfiguration.class})
class HandlerOrchestratorTest extends SendMessagePublisherTest {
    private static final String EXPECTED_HELP_MESSAGE = format("Help message%n");

    @Autowired
    HandlerOrchestrator orchestrator;

    @Autowired
    TokenHandler tokenHandler;

    @Autowired
    HelpHandler helpHandler;

    @Test
    void getHandler() {
        assertEquals(tokenHandler, orchestrator.getHandler(TOKEN.toString()));
        assertEquals(helpHandler, orchestrator.getHandler(HELP.toString()));
    }

    @Test
    void testOperateCommand_Authorized() {
        orchestrator.operate(1, HELP.toString());
        verifyPublishedMessage(1, EXPECTED_HELP_MESSAGE);
    }

    @Test
    void testOperateCommand_Unauthorized() {
        String input = ADMIN_TIME.toString();
        int userId = 2;

        orchestrator.operate(userId, input);

        verifyUnauthorizedInteraction(userId, input.replace("_", "-"));
    }

    @Test
    void getHandler_withException() {
        assertThrows(UnsupportedOperationException.class, () -> orchestrator.getHandler("/make pizza"));
    }

    @Test
    void testOperateCommand_noAction() {
        orchestrator.operate(1, "/make pizza");
        verifyNoInteractions(publisher);
    }

    @TestConfiguration
    static class UserServiceBeanConfig {
        @Bean
        UserService userService() {
            UserService userService = mock(UserService.class);
            when(userService.getOrCreate(1)).thenReturn(USER_1);
            when(userService.getOrCreate(2)).thenReturn(USER_2);
            return userService;
        }
    }
}
