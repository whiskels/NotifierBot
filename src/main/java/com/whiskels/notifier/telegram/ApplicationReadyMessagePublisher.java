package com.whiskels.notifier.telegram;

import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;

@Component
@RequiredArgsConstructor
class ApplicationReadyMessagePublisher {
    @Value("${telegram.bot.admin}")
    private String botAdmin;

    private final SendMessageCreationEventPublisher publisher;
    private final ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void report() {
        publisher.publish(builder(botAdmin)
                .line("*Successful boot report*")
                .line()
                .line("*Bot handlers:* %s", getBeanLabels(CommandHandler.class))
                .line()
                .line("*Slack reporters:* %s", getBeanLabels(SlackReporter.class))
                .build());
    }

    private <T> String getBeanLabels(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz).values().stream()
                .map(bean -> AopProxyUtils.ultimateTargetClass(bean).getSimpleName())
                .sorted(String::compareTo)
                .collect(COLLECTOR_COMMA_SEPARATED);
    }
}
