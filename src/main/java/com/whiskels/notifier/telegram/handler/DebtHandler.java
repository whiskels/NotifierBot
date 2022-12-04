package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.debt.DebtDto;
import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.security.Secured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Set;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.util.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_EMPTY_LINE;
import static com.whiskels.notifier.common.util.StreamUtil.filter;
import static com.whiskels.notifier.telegram.Command.GET_DEBT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;


@Service
@ConditionalOnBean(value = DebtDto.class, parameterizedContainer = ReportSupplier.class)
class DebtHandler implements ScheduledCommandHandler {
    private final String header;
    private final ReportSupplier<DebtDto> provider;

    public DebtHandler(@Value("${telegram.report.customer.debt.header:Overdue debts report on}") String header,
                       ReportSupplier<DebtDto> provider) {
        this.header = header;
        this.provider = provider;
    }

    @Override
    @Secured({MANAGER, HEAD, ADMIN})
    public SendMessage handle(User user, String message) {
        var data = provider.get();
        return builder(user)
                .line(ReportBuilder.builder(header + reportDate(data.getReportDate()))
                        .setActiveCollector(COLLECTOR_EMPTY_LINE)
                        .list(filter(data.getContent(), isValid(user)))
                        .build())
                .build();
    }

    @Override
    public Command getCommand() {
        return GET_DEBT;
    }

    private static Predicate<DebtDto> isValid(User user) {
        return debt -> {
            final Set<Role> roles = user.getRoles();
            return roles.contains(ADMIN)
                    || roles.contains(HEAD)
                    || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(debt.getAccountManager());
        };
    }
}
