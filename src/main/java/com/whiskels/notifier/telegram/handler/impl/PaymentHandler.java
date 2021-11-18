package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.operation.dto.PaymentDto;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.telegram.Command.GET_PAYMENT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Sends customer receivable report
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = GET_PAYMENT, requiredRoles = {ADMIN})
@ConditionalOnBean(value = PaymentDto.class, parameterizedContainer = Supplier.class)
@RequiredArgsConstructor
public class PaymentHandler extends AbstractBaseHandler {
    @Value("${telegram.report.customer.payment.header:Payment report on}")
    private String header;

    private final Supplier<PaymentDto> provider;

    @Override
    protected void handle(User user, String message) {
        publish(builder(user)
                .line(ReportBuilder.builder(header + reportDate(provider.lastUpdate()))
                        .list(provider.getData())
                        .build())
                .build());
    }
}
