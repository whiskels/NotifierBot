package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.debt.Debt;
import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.ScheduledCommandHandler;
import com.whiskels.notifier.telegram.builder.ReportBuilder;
import com.whiskels.notifier.telegram.domain.Role;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import com.whiskels.notifier.telegram.security.Secured;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.reportDate;
import static com.whiskels.notifier.common.util.FormatUtil.COLLECTOR_EMPTY_LINE;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.telegram.Command.GET_DEBT;
import static com.whiskels.notifier.telegram.builder.MessageBuilder.builder;
import static com.whiskels.notifier.telegram.domain.Role.*;


@Service
@ConditionalOnBean(value = Debt.class, parameterizedContainer = Supplier.class)
class DebtHandler implements ScheduledCommandHandler {
    private final String header;
    private final Supplier<Debt> provider;
    private final SendMessageCreationEventPublisher publisher;

    public DebtHandler(@Value("${telegram.report.customer.debt.header:Overdue debts report on}") String header,
                       Supplier<Debt> provider,
                       SendMessageCreationEventPublisher publisher) {
        this.header = header;
        this.provider = provider;
        this.publisher = publisher;
    }

    @Override
    @Secured({MANAGER, HEAD, ADMIN})
    public void handle(User user, String message) {
        publisher.publish(builder(user)
                .line(ReportBuilder.builder(header + reportDate(provider.lastUpdate()))
                        .setActiveCollector(COLLECTOR_EMPTY_LINE)
                        .list(filterAndSort(provider.getData(), isValid(user)))
                        .build())
                .build());
    }

    @Override
    public Command getCommand() {
        return GET_DEBT;
    }

    @Override
    public Set<Role> getRoles() {
        return Set.of(MANAGER, HEAD, ADMIN);
    }

    private static Predicate<Debt> isValid(User user) {
        return debt -> {
            final Set<Role> roles = user.getRoles();
            return roles.contains(ADMIN)
                    || roles.contains(HEAD)
                    || roles.contains(MANAGER) && user.getName().equalsIgnoreCase(debt.getAccountManager());
        };
    }
}
