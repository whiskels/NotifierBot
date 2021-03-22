package com.whiskels.notifier;

import com.whiskels.notifier.telegram.HandlerProvider;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.handler.impl.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class HandlerProviderTest {
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
        Assertions.assertEquals(handlerProvider.getHandlers().size(), HANDLER_COUNT);
    }

    @Test
    void isRightHandlerCalled() {
        final int commandsSize = TEST_COMMANDS.length;
        Assertions.assertTrue(commandsSize == HANDLERS.length);
        for (int i = 0; i < commandsSize; i++) {
            Assertions.assertEquals(getHandlerClass(TEST_COMMANDS[i]), HANDLERS[i]);
        }
    }

    private Class<? extends AbstractBaseHandler> getHandlerClass(String text) {
        return handlerProvider.getHandler(text).getClass();
    }
}
