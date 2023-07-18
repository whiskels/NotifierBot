package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.whiskels.notifier.telegram.util.TelegramUtil.groupByAllowedRoles;

@Component
class ScheduledHandlerOrchestrator {
    private final Map<Role, Set<ScheduledCommandHandler>> handlers;
    private final SendMessageCreationEventPublisher publisher;

    public ScheduledHandlerOrchestrator(Collection<ScheduledCommandHandler> handlers,
                                        SendMessageCreationEventPublisher publisher) {
        this.handlers = groupByAllowedRoles(handlers);
        this.publisher = publisher;
    }

    public void operate(Set<User> users) {
        users.forEach(user ->
                user.getRoles().stream()
                        .map(handlers::get)
                        .flatMap(Collection::stream)
                        .map(h -> h.handleScheduled(user))
                        .forEach(publisher::publish));
    }
}
