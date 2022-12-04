package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.operation.PaymentDto;
import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.CommandHandler;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.Secured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;
import static com.whiskels.notifier.telegram.Command.GET_PAYMENT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

@Service
@ConditionalOnBean(value = PaymentDto.class, parameterizedContainer = ReportSupplier.class)
class PaymentHandler implements CommandHandler {
    private final String header;
    private final ReportSupplier<PaymentDto> provider;

    public PaymentHandler(@Value("${telegram.report.customer.payment.header:Payment report on}") String header,
                          ReportSupplier<PaymentDto> provider) {
        this.header = header;
        this.provider = provider;
    }

    @Override
    @Secured(ADMIN)
    public SendMessage handle(User user, String message) {
        var data = provider.get();
        return builder(user)
                .line(ReportBuilder.builder(header + reportDate(data.getReportDate()))
                        .list(data.getContent())
                        .build())
                .build();
    }

    @Override
    public Command getCommand() {
        return GET_PAYMENT;
    }
}
