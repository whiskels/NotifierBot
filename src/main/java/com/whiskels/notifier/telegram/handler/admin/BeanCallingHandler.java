package com.whiskels.notifier.telegram.handler.admin;

import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.Secured;
import com.whiskels.notifier.telegram.util.TelegramUtil;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.whiskels.notifier.common.util.StreamUtil.collectToBulletListString;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractArguments;
import static java.util.stream.Collectors.toMap;


@Slf4j
abstract class BeanCallingHandler<T> implements CommandHandler {
    private final Map<String, T> labelToBeanMap;
    private final Function<T, ?> call;
    private final String messageHeader;

    public BeanCallingHandler(String messageHeader, List<T> callableBeans, Function<T, ?> call) {
        this.call = call;
        this.labelToBeanMap = callableBeans.stream().collect(toMap(TelegramUtil::getTelegramLabel, Function.identity()));
        this.messageHeader = messageHeader;
    }

    @Override
    @Secured(ADMIN)
    public SendMessage handle(User user, String message) {
        if (!message.contains(" ")) {
            return createMenuMessage(user);
        }
        String callbackQuery = extractArguments(message);
        return sendBeanCallResult(user, callbackQuery, callBean(callbackQuery));
    }

    private SendMessage createMenuMessage(User user) {
        MessageBuilder builder = builder(user)
                .line(messageHeader)
                .row();

        labelToBeanMap.keySet().forEach(label -> builder.buttonWithArguments(label, getCommand()).row());

        return builder.build();
    }

    private Object callBean(String callbackQuery) {
        return Optional.ofNullable(labelToBeanMap.get(callbackQuery))
                .map(call)
                .map(result -> (Collection.class.isAssignableFrom(result.getClass())
                        ? collectToBulletListString((Collection<?>) result, Object::toString)
                        : result.toString()))
                .orElse("Bean not found");
    }

    private SendMessage sendBeanCallResult(User user, String callbackQuery, Object o) {
        if (o instanceof Collection) {
            return MessageBuilder.builder(user)
                    .line(ReportBuilder.builder("Call result for " + callbackQuery)
                            .list((Collection<?>) o)
                            .build())
                    .build();
        }
        return MessageBuilder.builder(user)
                .line("Call result: " + o)
                .build();
    }
}
