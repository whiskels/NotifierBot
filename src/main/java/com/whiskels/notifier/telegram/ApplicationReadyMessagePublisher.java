package com.whiskels.notifier.telegram;

import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.events.SendMessageCreationEvent;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_COMMA_SEPARATED;

@Component
@RequiredArgsConstructor
@Profile("telegram-common")
@Slf4j
public class ApplicationReadyMessagePublisher {
    @Value("${telegram.bot.admin}")
    private String botAdmin;

    private final ApplicationEventPublisher publisher;
    private final ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void report() {
        publisher.publishEvent(new SendMessageCreationEvent(MessageBuilder.builder(botAdmin)
                .line("*Successful boot report*")
                .line()
                .line("*Bot handlers:* %s", getBeanLabels(AbstractBaseHandler.class))
                .line()
                .line("*Slack reporters:* %s", getBeanLabels(SlackReporter.class))
                .build()));
        log.debug("Start report sent to Admin");
    }

    private <T> String getBeanLabels(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz).values().stream()
                .map(bean -> bean.getClass().getSimpleName())
                .sorted(String::compareTo)
                .collect(COLLECTOR_COMMA_SEPARATED);
    }
}
