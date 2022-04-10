package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.telegram.Command.TOKEN;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;

@Service
@RequiredArgsConstructor
class TokenHandler implements CommandHandler {
    private final SendMessageCreationEventPublisher publisher;

    @Override
    public void handle(User user, String message) {
        publisher.publish(builder(user)
                .line("Your token is *%s*", user.getChatId())
                .line("Your roles are: %s", user.getRoles().stream()
                        .map(Enum::toString)
                        .collect(COLLECTOR_COMMA_SEPARATED))
                .build());
    }

    @Override
    public Command getCommand() {
        return TOKEN;
    }
}
