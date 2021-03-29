package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.receivable.dto.ReceivableDto;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import com.whiskels.notifier.telegram.security.AuthorizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;

import static com.whiskels.notifier.common.FormatUtil.COLLECTOR_NEW_LINE;
import static com.whiskels.notifier.common.StreamUtil.alwaysTruePredicate;
import static com.whiskels.notifier.external.receivable.util.ReceivableUtil.CATEGORY_REVENUE;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.create;
import static com.whiskels.notifier.telegram.builder.ReportBuilder.withHeader;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;
import static java.time.LocalDate.now;

/**
 * Sends customer receivable report
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = "/GET_RECEIVABLE", message = "Get customer receivables", requiredRoles = {ADMIN})
@ConditionalOnBean(value = ReceivableDto.class, parameterizedContainer = DataProvider.class)
public class ReceivableHandler extends AbstractBaseHandler {
    private final DataProvider<ReceivableDto> provider;
    private final Clock clock;

    public ReceivableHandler(AuthorizationService authorizationService,
                             ApplicationEventPublisher publisher,
                             DataProvider<ReceivableDto> provider,
                             Clock clock) {
        super(authorizationService, publisher);
        this.clock = clock;
        this.provider = provider;
    }

    @Override
    protected void handle(User user, String message) {
        log.debug("Preparing /GET_RECEIVABLE");

        publish(create(user)
                .line(withHeader(CATEGORY_REVENUE, now(clock))
                        .list(provider.get(), alwaysTruePredicate(), COLLECTOR_NEW_LINE)
                        .build())
                .build());
    }
}
