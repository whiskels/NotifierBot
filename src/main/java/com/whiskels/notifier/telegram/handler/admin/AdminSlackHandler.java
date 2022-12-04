package com.whiskels.notifier.telegram.handler.admin;

import com.whiskels.notifier.slack.SlackWebHookExecutor;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import com.whiskels.notifier.telegram.Command;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.whiskels.notifier.telegram.Command.ADMIN_SLACK;
import static com.whiskels.notifier.telegram.util.TelegramUtil.getTelegramLabel;

@Service
@ConditionalOnBean(SlackReporter.class)
class AdminSlackHandler extends BeanCallingHandler<SlackReporter<?>> {
    private final SlackWebHookExecutor executor;

    public AdminSlackHandler(List<SlackReporter<?>> reporters, SlackWebHookExecutor executor) {
        super("Manual slack reporter activation", reporters,
                reporter -> {
                    executor.execute(reporter.prepare());
                    return "Called " + getTelegramLabel(reporter);
                });
        this.executor = executor;
    }

    @Override
    public Command getCommand() {
        return ADMIN_SLACK;
    }
}
