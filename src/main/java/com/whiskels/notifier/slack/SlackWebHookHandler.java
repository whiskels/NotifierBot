package com.whiskels.notifier.slack;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.MonthlyReporter;

import java.io.IOException;

import static com.slack.api.webhook.WebhookPayloads.payload;

/**
 * Interface that allows interaction with Slack's ability to send updates using webhooks
 */
public interface SlackWebHookHandler {
    /**
     * Creates JSON payload and sends it to the webhook passed as method argument.
     *
     * @param webHook URL to the webhook
     * @param message String that will be sent in JSON payload
     */
    default String createAndSendPayload(String webHook, String message) {
        try {
            final Slack slack = Slack.getInstance();
            WebhookResponse response = slack.send(webHook, payload(p -> p.text(message)));
            return response.toString();
        } catch (IOException e) {
            return String.format("Error while sending %s to webhook: %s", message, webHook);
        }
    }

    default <T> String sendDailyReport(String webHook, DailyReporter<T> provider) {
        return createAndSendPayload(webHook, provider.dailyReport());
    }

    default <T> String sendMonthlyReport(String webHook, MonthlyReporter<T> provider) {
        return createAndSendPayload(webHook, provider.monthlyReport());
    }
}
