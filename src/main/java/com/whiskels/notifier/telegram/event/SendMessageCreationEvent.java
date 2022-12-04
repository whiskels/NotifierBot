package com.whiskels.notifier.telegram.event;

import com.whiskels.notifier.common.CreationEvent;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.Nullable;

public class SendMessageCreationEvent extends CreationEvent<SendMessage> {
    public SendMessageCreationEvent(@Nullable SendMessage object) {
        super(object);
    }
}
