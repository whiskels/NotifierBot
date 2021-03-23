package com.whiskels.notifier.telegram;

import com.whiskels.notifier.DisabledDataSourceConfiguration;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.handler.impl.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static com.whiskels.notifier.telegram.domain.Role.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class HandlerProviderTest extends DisabledDataSourceConfiguration {
    private static final int HANDLER_COUNT = 12;
    private static final String[] TEST_COMMANDS = new String[]{"/ADMIN_PROMOTE", "/ADMIN_TIME", "/ADMIN_NAME",
    "/BIRTHDAY", "/GET", "/GET_RECEIVABLE", "/HELP", "/SCHEDULE", "/SCHEDULE_CLEAR", "/SCHEDULE_HELP",
            "/TOKEN"};
    private static final String START_COMMAND = "/START";
    private static final Class<? extends AbstractBaseHandler>[] HANDLERS = new Class[]{
            AdminPromoteHandler.class, AdminTimeHandler.class, AdminUpdateNameHandler.class,
            BirthdayHandler.class, CustomerDebtHandler.class, CustomerReceivableHandler.class,
            HelpHandler.class, ScheduleAddHandler.class, ScheduleClearHandler.class, ScheduleHelpHandler.class,
            TokenHandler.class
    };

    @Autowired
    HandlerProvider handlerProvider;

    @Test
    void areAllHandlersAdded() {
        assertEquals(HANDLER_COUNT, handlerProvider.getHandlers().size(), "Different number of handlers is present");
    }

    @Test
    void isRightHandlerCalled() {
        final int commandsSize = TEST_COMMANDS.length;
        assertTrue(commandsSize == HANDLERS.length);
        for (int i = 0; i < commandsSize; i++) {
            assertEquals(HANDLERS[i], getHandlerClass(TEST_COMMANDS[i]), "Wrong handler");
        }

        assertEquals(BirthdayHandler.class, handlerProvider.getSchedulableHandler(Set.of(HR)).getClass());
        assertEquals(CustomerDebtHandler.class, handlerProvider.getSchedulableHandler(Set.of(MANAGER)).getClass());
        assertEquals(CustomerDebtHandler.class, handlerProvider.getSchedulableHandler(Set.of(HEAD)).getClass());
        assertEquals(CustomerDebtHandler.class, handlerProvider.getSchedulableHandler(Set.of(ADMIN)).getClass());

    }

    private Class<? extends AbstractBaseHandler> getHandlerClass(String text) {
        return handlerProvider.getHandler(text).getClass();
    }
}
