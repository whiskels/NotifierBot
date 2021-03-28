package com.whiskels.notifier.slack;

import com.whiskels.notifier.common.CreationEvent;

public class SlackPayloadCreatedEvent extends CreationEvent<SlackPayload> {
    public SlackPayloadCreatedEvent(SlackPayload object) {
        super(object);
    }
}
