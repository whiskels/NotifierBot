package com.whiskels.notifier.telegram.handler.admin;

import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.telegram.Command;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import static com.whiskels.notifier.telegram.Command.ADMIN_SLACK;

@Service
@ConditionalOnBean(SlackReporter.class)
class AdminSlackHandler extends BeanCallingHandler<SlackReporter> {
    public AdminSlackHandler() {
        super(null, SlackReporter::report, "Manual slack reporter activation");
    }

    @Override
    public Command getCommand() {
        return ADMIN_SLACK;
    }
}
