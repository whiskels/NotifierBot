package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.UserService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.stream.Collectors;

import static com.whiskels.notifier.model.Role.ADMIN;
import static com.whiskels.notifier.util.ParsingUtil.extractArguments;

/**
 * Notifies all bot users with a message from admin
 * <p>
 * Available to: Admin
 */
@Component
@Slf4j
@BotCommand(command = "/ADMIN_MESSAGE", requiredRoles = {ADMIN})
@Profile({"telegram", "telegram-test"})
public class AdminMessageHandler extends AbstractUserHandler {
    public AdminMessageHandler(UserService userService) {
        super(userService);
    }

    @Override
    public List<BotApiMethod<Message>> handle(User admin, String text) {
        log.debug("Preparing /ADMIN_MESSAGE");
        List<BotApiMethod<Message>> messagesToSend = userService.getUsers()
                .stream()
                .map(user -> MessageBuilder.create(user)
                        .line(extractArguments(text))
                        .build())
                .collect(Collectors.toList());

        log.debug("Prepared {} messages", messagesToSend.size());
        messagesToSend.add(MessageBuilder.create(admin)
                .line("Notified %d users", messagesToSend.size())
                .build());

        return messagesToSend;
    }


}
