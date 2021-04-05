package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static com.whiskels.notifier.telegram.Command.ADMIN_SLACK;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static com.whiskels.notifier.telegram.util.ParsingUtil.extractArguments;

@Profile("telegram-dev")
@Slf4j
@BotCommand(command = ADMIN_SLACK, requiredRoles = {ADMIN})
public class AdminSlackHandler extends AbstractBaseHandler {
    @Autowired
    private List<SlackReporter> slackReporterList;

    public AdminSlackHandler(AuthorizationService authorizationService, ApplicationEventPublisher publisher) {
        super(authorizationService, publisher);
    }

    @Override
    protected void handle(User user, String message) {
        if (!message.contains(" ")) {
            MessageBuilder builder = create(user)
                    .line("Manual slack reporter activation")
                    .row();
            slackReporterList.forEach(rep -> builder.buttonWithArguments(rep.getClass().getSimpleName(), ADMIN_SLACK));
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
