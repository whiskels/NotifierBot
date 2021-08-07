package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.debt.service.CustomerDebtDataProvider;
import com.whiskels.notifier.telegram.domain.Schedule;
import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import com.whiskels.notifier.telegram.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {ScheduleAddHandler.class, DebtHandler.class})
@MockBean(classes = {CustomerDebtDataProvider.class, ScheduleService.class})
class ScheduleAddHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_AUTH = format("*Your current schedule:*%n" +
            "Empty%n" +
            "%n" +
            "Choose from available options or add preferred time to [/schedule](/schedule) command:%n");
    private static final String EXPECTED_AUTH_KEYBOARD = "InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=9:00, url=null, callbackData=/SCHEDULE 9:00, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null), InlineKeyboardButton(text=12:00, url=null, callbackData=/SCHEDULE 12:00, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null), InlineKeyboardButton(text=15:00, url=null, callbackData=/SCHEDULE 15:00, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null)], [InlineKeyboardButton(text=Clear schedule, url=null, callbackData=/SCHEDULE_CLEAR, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null), InlineKeyboardButton(text=Help, url=null, callbackData=/SCHEDULE_HELP, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null)]])";
    private static final String EXPECTED_AUTH_VALID_COMMAND = format("Scheduled status messages to%nbe sent daily at *10:00*%n");
    private static final String EXPECTED_AUTH_INVALID_COMMAND = format("You've entered invalid time%n" +
            "Please try again%n");

    @Autowired
    private ScheduleAddHandler scheduleAddHandler;

    @Autowired
    private ScheduleService scheduleService;

    @BeforeEach
    public void setHandler() {
        handler = scheduleAddHandler;
    }

    @Test
    void testScheduleAddHandler_Authorized() {
        testInteraction(USER_1, EXPECTED_AUTH, EXPECTED_AUTH_KEYBOARD);

        verify(scheduleService).getSchedule(1);
        verifyNoMoreInteractions(scheduleService);
    }

    @Test
    void testScheduleAddHandler_AuthorizedWithValidCommand() {
        handler.authorizeAndHandle(USER_1, "/schedule 1000");

        verify(scheduleService).addSchedule(any(Schedule.class), any(Integer.class));
        verifyNoMoreInteractions(scheduleService);

        verify(publisher).publishEvent(captor.capture());
        SendMessage actual = getCapturedMessage();
        assertEquals(String.valueOf(USER_1.getChatId()), actual.getChatId());
        assertEquals(EXPECTED_AUTH_VALID_COMMAND, actual.getText());
    }

    @Test
    void testScheduleAddHandler_AuthorizedWithInvalidCommand() {
        handler.authorizeAndHandle(USER_1, "/schedule 10000");

        verifyNoInteractions(scheduleService);

        verify(publisher).publishEvent(captor.capture());
        SendMessage actual = getCapturedMessage();
        assertEquals(String.valueOf(USER_1.getChatId()), actual.getChatId());
        assertEquals(EXPECTED_AUTH_INVALID_COMMAND, actual.getText());
    }

    @Test
    void testScheduleAddHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2, "/schedule 1000");
    }
}
