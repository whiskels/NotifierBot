package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractAdminCallHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import static com.whiskels.notifier.telegram.Command.ADMIN_SLACK;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

@Slf4j
@BotCommand(command = ADMIN_SLACK, requiredRoles = {ADMIN})
@ConditionalOnBean(SlackReporter.class)
public class AdminSlackHandler extends AbstractAdminCallHandler<SlackReporter> {
    public AdminSlackHandler() {
        super(null, SlackReporter::report, "Manual slack reporter activation");
    }
}
