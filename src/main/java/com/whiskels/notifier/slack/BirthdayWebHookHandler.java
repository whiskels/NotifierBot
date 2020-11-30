package com.whiskels.slack;

import com.whiskels.notifier.service.EmployeeService;
import com.whiskels.notifier.slack.SlackWebHookHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"slack", "slack-test"})
@Slf4j
public class BirthdayWebHookHandler implements SlackWebHookHandler {
    @Value("${slack.employee.birthday.webhook}")
    private String webHook;

    private final EmployeeService employeeService;

    @Scheduled(cron = "${slack.employee.birthday.dailyCron}")
    public void dailyPayload() {
        String result = createAndSendPayload(webHook, employeeService.dailyReport());
        log.info(result);
    }

    @Scheduled(cron = "${slack.employee.birthday.monthlyCron}")
    public void monthlyPayload() {
        String result = createAndSendPayload(webHook, employeeService.monthlyReport());
        log.info(result);
    }
}
