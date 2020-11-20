package com.whiskels.notifier.slack;

import com.whiskels.notifier.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile({"slack", "test"})
public class BirthdayHandler extends AbstractSlackHandler {
    @Value("${slack.employee.birthday.webhook}")
    private String webHook;

    private final EmployeeService employeeService;

    @Scheduled(cron = "${slack.employee.birthday.dailyCron}")
    public void dailyPayload() {
        createAndSendPayload(webHook, employeeService.getDailyBirthdayInfo());
    }

    @Scheduled(cron = "${slack.employee.birthday.monthlyCron}")
    public void monthlyPayload() {
        createAndSendPayload(webHook, employeeService.getMonthlyBirthdayInfo());
    }
}
