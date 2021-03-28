package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DailyReporter;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Sends customer receivable report
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = "/GET_RECEIVABLE", message = "Get customer receivables", requiredRoles = {ADMIN})
@ConditionalOnBean(value = ReceivableDto.class, parameterizedContainer = DailyReporter.class)
@RequiredArgsConstructor
public class ReceivableHandler extends AbstractBaseHandler {
    private final DailyReporter<ReceivableDto> reporter;

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /GET_RECEIVABLE");

        publish(create(user)
                .line(reporter.dailyReport())
                .build());
    }
}

