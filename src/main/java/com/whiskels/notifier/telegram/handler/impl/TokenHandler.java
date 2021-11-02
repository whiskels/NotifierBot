package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.extern.slf4j.Slf4j;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.telegram.Command.TOKEN;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;

/**
 * Sends user his token
 * <p>
 * Available to: everyone
 */
@Slf4j
@BotCommand(command = TOKEN)
public class TokenHandler extends AbstractBaseHandler {
    @Override
    protected void handle(User user, String message) {
        publish(builder(user)
                .line("Your token is *%s*", user.getChatId())
                .line("Your roles are: %s", user.getRoles().stream()
                        .map(Enum::toString)
                        .collect(COLLECTOR_COMMA_SEPARATED))
                .build());
    }
}
