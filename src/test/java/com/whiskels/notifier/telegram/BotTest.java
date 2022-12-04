package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.event.MessageReceivedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import static com.whiskels.notifier.telegram.UpdateTestData.HELP_MESSAGE_JSON;
import static com.whiskels.notifier.telegram.UpdateTestData.update;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BotTest {
    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);
    private final Bot bot = new Bot("foo", "bar", publisher);

    @Test
    void whenUpdateReceived_shouldPublishEvent() {
        bot.onUpdateReceived(update(HELP_MESSAGE_JSON));

        verify(publisher, times(1)).publishEvent(any(MessageReceivedEvent.class));
        verifyNoMoreInteractions(publisher);
    }
}
