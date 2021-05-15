package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.events.UpdateCreationEvent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import static com.whiskels.notifier.telegram.UpdateTestData.HELP_MESSAGE_JSON;
import static com.whiskels.notifier.telegram.UpdateTestData.update;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "telegram.bot.token=123")
class BotTest {
    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);
    private final Bot bot = new Bot(publisher);

    @Test
    void whenUpdateReceived_shouldPublishEvent() {
        bot.onUpdateReceived(update(HELP_MESSAGE_JSON));

        verify(publisher, times(1)).publishEvent(any(UpdateCreationEvent.class));
        verifyNoMoreInteractions(publisher);
    }
}
