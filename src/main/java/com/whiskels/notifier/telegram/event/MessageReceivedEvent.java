package com.whiskels.notifier.telegram.event;

import com.whiskels.notifier.common.CreationEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageReceivedEvent extends CreationEvent<Update> {
    public MessageReceivedEvent(Update object) {
        super(object);
    }
}
