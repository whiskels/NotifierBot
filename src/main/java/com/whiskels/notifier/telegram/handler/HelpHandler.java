package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.whiskels.notifier.telegram.Command.HELP;
import static com.whiskels.notifier.telegram.domain.Role.UNAUTHORIZED;
import static com.whiskels.notifier.telegram.util.TelegramUtil.toRoleMap;

@Primary
@Service
class HelpHandler implements CommandHandler {
    private final String botName;
    private final Map<Role, Set<CommandHandler>> handlerMapping;
    private final SendMessageCreationEventPublisher publisher;

    public HelpHandler(@Value("${telegram.bot.name:TelegramNotifierBot}") String botName,
                       Collection<CommandHandler> handlers,
                       SendMessageCreationEventPublisher publisher) {
        this.botName = botName;
        this.handlerMapping = toRoleMap(handlers);
        this.publisher = publisher;
    }

    @Override
    public void handle(User user, String message) {
        MessageBuilder builder = MessageBuilder.builder(user)
                .line("Hello. I'm *%s*", botName)
                .line("Here are your available commands");
        generateButtons(builder, user);
        publisher.publish(builder.build());
    }

    @Override
    public Command getCommand() {
        return HELP;
    }

    private void generateButtons(MessageBuilder builder, User user) {
        Set<Role> roles = user.getRoles();
        roles.add(UNAUTHORIZED);
        roles.stream()
                .map(role -> handlerMapping.getOrDefault(role, new HashSet<>()))
                .flatMap(Collection::stream)
                .map(CommandHandler::getCommand)
                .sorted(Comparator.comparing(Command::getOrder))
                .forEach(command -> {
                    String description = command.getDescription();
                    if (!description.isEmpty())
                        builder.row().button(description, command);
                });
    }
}
