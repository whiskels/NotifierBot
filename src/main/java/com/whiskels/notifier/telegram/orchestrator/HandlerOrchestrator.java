package com.whiskels.notifier.telegram.orchestrator;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static com.whiskels.notifier.telegram.util.ParsingUtil.extractCommand;
import static java.util.stream.Collectors.toMap;

@Component
@Profile("telegram-common")
public class HandlerOrchestrator {
    private final Map<Command, CommandHandler> handlers;
    private final CommandHandler defaultHandler;
    private final UserService userService;

    public HandlerOrchestrator(UserService userService, Collection<CommandHandler> handlers, CommandHandler defaultHandler) {
        this.userService = userService;
        this.defaultHandler = defaultHandler;
        this.handlers = handlers.stream().collect(toMap(CommandHandler::getCommand, Function.identity()));
    }

    public void operate(Long userId, String message) {
        handlers.getOrDefault(extractCommand(message), defaultHandler)
                .handle(userService.getOrCreate(userId), message);
    }
}
