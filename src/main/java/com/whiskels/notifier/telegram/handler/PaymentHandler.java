package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.operation.dto.PaymentDto;
import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import com.whiskels.notifier.telegram.security.Secured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.telegram.Command.GET_PAYMENT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

@Service
@ConditionalOnBean(value = PaymentDto.class, parameterizedContainer = Supplier.class)
class PaymentHandler implements CommandHandler {
    private final String header;
    private final SendMessageCreationEventPublisher publisher;
    private final Supplier<PaymentDto> provider;

    public PaymentHandler(@Value("${telegram.report.customer.payment.header:Payment report on}") String header,
                          SendMessageCreationEventPublisher publisher,
                          Supplier<PaymentDto> provider) {
        this.header = header;
        this.publisher = publisher;
        this.provider = provider;
    }

    @Override
    @Secured(ADMIN)
    public void handle(User user, String message) {
        publisher.publish(builder(user)
                .line(ReportBuilder.builder(header + reportDate(provider.lastUpdate()))
                        .list(provider.getData())
                        .build())
                .build());
    }

    @Override
    public Command getCommand() {
        return GET_PAYMENT;
    }
}
