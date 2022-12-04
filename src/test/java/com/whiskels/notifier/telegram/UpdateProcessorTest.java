package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.event.MessageReceivedEvent;
import com.whiskels.notifier.telegram.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.whiskels.notifier.telegram.Command.HELP;
import static com.whiskels.notifier.telegram.Command.TOKEN;
import static com.whiskels.notifier.telegram.UpdateTestData.*;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {UpdateProcessor.class})
@MockBean(classes = {HandlerOrchestrator.class, UserService.class})
class UpdateProcessorTest {
    @Autowired
    private HandlerOrchestrator orchestrator;
    @Autowired
    private UpdateProcessor listener;
    @Autowired
    private UserService userService;

    @BeforeEach
    void initMocks() {
        when(userService.getOrCreate(any())).thenReturn(USER_1);
    }

    @Test
    void givenValidUpdateWithText_shouldCallOrchestrator() {
        Update helpUpdate = update(HELP_MESSAGE_JSON);

        listener.handleUpdate(new MessageReceivedEvent(helpUpdate));

        verify(orchestrator).operate(USER_1, HELP.toString());
        verifyNoMoreInteractions(orchestrator);
    }

    @Test
    void givenValidUpdateWithCallBackData_shouldCallOrchestrator() {
        Update helpUpdate = update(TOKEN_QUERY_JSON);

        listener.handleUpdate(new MessageReceivedEvent(helpUpdate));

        verify(orchestrator).operate(USER_1, TOKEN.toString());
        verifyNoMoreInteractions(orchestrator);
    }
}
