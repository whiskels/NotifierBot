package com.whiskels.notifier.telegram.events;

import com.whiskels.notifier.common.CreationEvent;
import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateCreationEvent extends CreationEvent<Update> {
    public UpdateCreationEvent(Update object) {
        super(object);
    }
}
