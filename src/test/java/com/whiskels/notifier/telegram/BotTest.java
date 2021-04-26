package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.events.UpdateCreationEvent;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "telegram.bot.token=123")
class BotTest {
    private final ApplicationEventPublisher publisher = Mockito.mock(ApplicationEventPublisher.class);
    private final Bot bot = new Bot(publisher);

    @Test
    void whenUpdateReceived_shouldPublishEvent() {
        bot.onUpdateReceived(new Update());

        verify(publisher, times(1)).publishEvent(any(UpdateCreationEvent.class));
        verifyNoMoreInteractions(publisher);
    }

    //TODO implement
    @Ignore
    @Test
    void testBotMessageExecution() {
    }
}
