package com.whiskels.notifier.telegram.orchestrator;

import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static com.whiskels.notifier.telegram.util.TelegramUtil.toRoleMap;

@Component
@Profile("telegram-common")
public class ScheduledHandlerOrchestrator {
    private final Map<Role, Set<ScheduledCommandHandler>> handlers;

    public ScheduledHandlerOrchestrator(Collection<ScheduledCommandHandler> handlers) {
        this.handlers = toRoleMap(handlers);
    }

    public void operate(User user) {
        user.getRoles().stream()
                .map(handlers::get)
                .flatMap(Collection::stream)
                .forEach(h -> h.handleScheduled(user));
    }
}
