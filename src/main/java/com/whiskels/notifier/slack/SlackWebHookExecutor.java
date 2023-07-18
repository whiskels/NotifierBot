package com.whiskels.notifier.slack;

import com.slack.api.Slack;
import com.whiskels.notifier.slack.reporter.SlackReporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@ConditionalOnBean(SlackReporter.class)
public class SlackWebHookExecutor {
    private final Slack slack = Slack.getInstance();

    public int execute(SlackPayload payload) {
        if (payload == null) return HttpStatus.NO_CONTENT.value();

        try {
            var response = slack.send(payload.getUrl(), payload.getData());
            log.info(String.valueOf(response));
            return response.getCode();
        } catch (IOException e) {
            log.error("Error while sending {} to webhook: {}", payload.getData(), payload.getUrl());
            return HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
    }
}
