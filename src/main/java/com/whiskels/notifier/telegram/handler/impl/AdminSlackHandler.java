package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static com.whiskels.notifier.telegram.Command.ADMIN_SLACK;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractArguments;


@Slf4j
@BotCommand(command = ADMIN_SLACK, requiredRoles = {ADMIN})
@ConditionalOnBean(SlackReporter.class)
public class AdminSlackHandler extends AbstractBaseHandler {
    @Autowired
    @Setter
    @SuppressWarnings("rawtypes")
    private List<SlackReporter> slackReporterList;

    public AdminSlackHandler(AuthorizationService authorizationService, ApplicationEventPublisher publisher) {
        super(authorizationService, publisher);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected void handle(User user, String message) {
        if (!message.contains(" ")) {
            MessageBuilder builder = builder(user)
                    .line("Manual slack reporter activation")
                    .row();
            slackReporterList.forEach(rep -> builder.buttonWithArguments(rep.getClass().getSimpleName(), ADMIN_SLACK)
                    .row());
            publish(builder.build());
        } else {
            String reporterName = extractArguments(message);
            for (SlackReporter slackReporter : slackReporterList) {
                if (slackReporter.getClass().getSimpleName().equalsIgnoreCase(reporterName)) {
                    slackReporter.report();
                }
            }
        }
    }
}
