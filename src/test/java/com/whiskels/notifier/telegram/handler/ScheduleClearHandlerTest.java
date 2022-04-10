package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ScheduleClearHandler.class, DebtHandler.class})
@MockBean(classes = {ScheduleService.class})
class ScheduleClearHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_AUTH = format("Your schedule (100) was cleared");

    @Autowired
    private CommandHandler scheduleClearHandler;

    @Autowired
    private ScheduleService scheduleService;

    @BeforeEach
    public void setHandler() {
        handler = scheduleClearHandler;
    }

    @Test
    void testScheduleClearHandler_Authorized() {
        when(scheduleService.clear(USER_1.getChatId())).thenReturn(100);

        testInteraction(USER_1, EXPECTED_AUTH);
    }

    @Test
    void testScheduleClearHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2);
    }
}
