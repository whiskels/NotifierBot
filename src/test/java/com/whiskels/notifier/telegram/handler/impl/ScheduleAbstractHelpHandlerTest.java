package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;

@SpringBootTest(classes = {ScheduleHelpHandler.class, DebtHandler.class})
//@MockBean(CustomerDebtDataProvider.class)
class ScheduleAbstractHelpHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_AUTH = format("*Help message for /schedule command*%n" +
            "%n" +
            "[/schedule *time*](/schedule time) - set daily message at time. Examples: %n" +
            "   /schedule 1 - 01:00%n" +
            "   /schedule 10 - 10:00%n" +
            "   /schedule 1230 - 12:30%n" +
            "Please note that daily messages are not sent on *sundays and saturdays*!%n");

    @Autowired
    private ScheduleHelpHandler scheduleHelpHandler;

    @BeforeEach
    public void setHandler() {
        handler = scheduleHelpHandler;
    }

    @Test
    void testScheduleHelpHandler_Authorized() {
        testInteraction(USER_1, EXPECTED_AUTH);
    }

    @Test
    void testScheduleHelpHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2);
    }
}

