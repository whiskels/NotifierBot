package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.telegram.Command.TOKEN;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;

@Service
@RequiredArgsConstructor
class TokenHandler implements CommandHandler {
    @Override
    public SendMessage handle(User user, String message) {
        return builder(user)
                .line("Your token is *%s*", user.getChatId())
                .line("Your roles are: %s", user.getRoles().stream()
                        .map(Enum::toString)
                        .collect(COLLECTOR_COMMA_SEPARATED))
                .build();
    }

    @Override
    public Command getCommand() {
        return TOKEN;
    }
}
