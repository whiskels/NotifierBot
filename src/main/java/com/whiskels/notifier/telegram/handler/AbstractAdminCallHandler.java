package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractArguments;
import static com.whiskels.notifier.telegram.util.TelegramUtil.*;


@Slf4j
@RequiredArgsConstructor
public class AbstractAdminCallHandler<T> extends AbstractBaseHandler {
    @Setter(onMethod = @__(@Autowired))
    private List<T> callableBeans;
    private final Function<T, ?> callFunction;
    private final Consumer<T> consumerFunction;
    private final String messageHeader;

    @Override
    protected void handle(User user, String message) {
        if (!message.contains(" ")) {
            publish(createMenuMessage(user));
        } else {
            String callbackQuery = extractArguments(message);
            Object result = callBean(callbackQuery);
            sendBeanCallResult(user, callbackQuery, result);
        }
    }

    private SendMessage createMenuMessage(User user) {
        MessageBuilder builder = builder(user)
                .line(messageHeader)
                .row();
        addButtons(builder);
        return builder.build();
    }

    private void addButtons(MessageBuilder builder) {
        callableBeans.forEach(
                rep -> builder.buttonWithArguments(getTelegramLabel(rep), getCommandFromClass(this)).row()
        );
    }

    private Object callBean(String callbackQuery) {
        for (T callableBean : callableBeans) {
            if (isCalledInCallback(callableBean, callbackQuery)) {
                log.info("Called {} from Telegram", callableBean);
                if (callFunction != null) {
                    return callFunction.apply(callableBean);
                } else if (consumerFunction != null) {
                    consumerFunction.accept(callableBean);
                    return "Called " + callbackQuery;
                }
            }
        }
        log.error("No bean found for: {}", callbackQuery);
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void sendBeanCallResult(User user, String callbackQuery, Object o) {
        if (o == null) return;

        SendMessage sendMessage;
        if (o instanceof Collection) {
            sendMessage = MessageBuilder.builder(user)
                    .line(ReportBuilder.builder("Call result for " + callbackQuery)
                    .list((Collection) o)
                    .build())
                    .build();
        } else {
            sendMessage = MessageBuilder.builder(user)
                    .line("Call result: " + o)
                    .build();
        }

        publish(sendMessage);
    }
}
