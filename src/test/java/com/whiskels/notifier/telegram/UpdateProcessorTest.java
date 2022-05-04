package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.event.UpdateCreationEvent;
import com.whiskels.notifier.telegram.orchestrator.HandlerOrchestrator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.whiskels.notifier.telegram.Command.HELP;
import static com.whiskels.notifier.telegram.Command.TOKEN;
import static com.whiskels.notifier.telegram.UpdateTestData.*;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@SpringBootTest(classes = {UpdateProcessor.class})
@MockBean(classes = {HandlerOrchestrator.class})
class UpdateProcessorTest {
    @Autowired
    private HandlerOrchestrator orchestrator;
    @Autowired
    private UpdateProcessor listener;

    @Test
    void givenValidUpdateWithText_shouldCallOrchestrator() {
        Update helpUpdate = update(HELP_MESSAGE_JSON);

        listener.handleUpdate(new UpdateCreationEvent(helpUpdate));

        verify(orchestrator).operate(USER_1.getChatId(), HELP.toString().toLowerCase());
        verifyNoMoreInteractions(orchestrator);
    }

    @Test
    void givenValidUpdateWithCallBackData_shouldCallOrchestrator() {
        Update helpUpdate = update(TOKEN_QUERY_JSON);

        listener.handleUpdate(new UpdateCreationEvent(helpUpdate));

        verify(orchestrator).operate(USER_1.getChatId(), TOKEN.toString());
        verifyNoMoreInteractions(orchestrator);
    }
}
