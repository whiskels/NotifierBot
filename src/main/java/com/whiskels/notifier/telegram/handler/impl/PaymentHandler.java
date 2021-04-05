package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.operation.dto.FinancialOperationDto;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_NEW_LINE;
import static com.whiskels.notifier.external.operation.util.FinOperationUtil.CATEGORY_PAYMENT;
import static com.whiskels.notifier.telegram.Command.GET_PAYMENT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.builder.ReportBuilder.withHeader;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Sends customer receivable report
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = GET_PAYMENT, requiredRoles = {ADMIN})
@ConditionalOnBean(value = FinancialOperationDto.class, parameterizedContainer = DataProvider.class)
public class PaymentHandler extends AbstractBaseHandler {
    private final DataProvider<FinancialOperationDto> provider;

    public PaymentHandler(AuthorizationService authorizationService,
                          ApplicationEventPublisher publisher,
                          DataProvider<FinancialOperationDto> provider) {
        super(authorizationService, publisher);
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /GET_PAYMENT");

        publish(create(user)
                .line(withHeader(CATEGORY_PAYMENT + " report", provider.lastUpdate())
                        .list(provider.get(), COLLECTOR_NEW_LINE)
                        .build())
                .build());
    }
}
