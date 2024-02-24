package com.whiskels.notifier.reporting.service.customer.birthday.convert;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.reporting.domain.HasBirthday;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.SimpleReport;
import com.whiskels.notifier.reporting.service.customer.birthday.domain.CustomerBirthdayInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.whiskels.notifier.utilities.collections.StreamUtil.collectToBulletListString;
import static com.whiskels.notifier.utilities.formatters.DateTimeFormatter.BIRTHDAY_FORMATTER;

@Slf4j
@AllArgsConstructor
public class CustomerBirthdayInfoReportMessageConverter implements ReportMessageConverter<CustomerBirthdayInfo> {
    private final List<ReportContext> contexts;
    private final String noData;

    @Nonnull
    @Override
    public Iterable<Payload> convert(@Nonnull ReportData<CustomerBirthdayInfo> data) {
        return contexts.stream()
                .map(context -> createReport(data, context))
                .filter(Objects::nonNull)
                .toList();
    }

    private Payload createReport(ReportData<CustomerBirthdayInfo> data, ReportContext context) {
        log.debug("Creating customer birthday event payload");
        List<CustomerBirthdayInfoDto> birthdays = data.data().stream()
                .filter(dto -> context.getPredicate().test(dto, data.requestDate()))
                .map(CustomerBirthdayInfoDto::from)
                .sorted(HasBirthday.comparator())
                .collect(Collectors.toList());

        if (context.getSkipEmptyPredicate().test(data.requestDate()) && birthdays.isEmpty()) {
            log.warn("Customer birthday payload creation aborted: empty event lists and skipEmpty flag is true: {}", context.getClass().getSimpleName());
            return null;
        }
        var message = birthdays.isEmpty() ? noData : collectToBulletListString(birthdays, CustomerBirthdayInfoDto::toReportString);
        return new SimpleReport(context.getHeaderMapper().apply(data.requestDate()), message).toSlackPayload();
    }

    @Builder
    private record CustomerBirthdayInfoDto(String company, String name, String surname,
                                           LocalDate birthday) implements HasBirthday {
        public static CustomerBirthdayInfoDto from(CustomerBirthdayInfo c) {
            return CustomerBirthdayInfoDto.builder()
                    .company(c.company())
                    .name(c.name())
                    .surname(c.surname())
                    .birthday(c.birthday())
                    .build();
        }

        public String toReportString() {
            return STR."\{name} \{surname} \{BIRTHDAY_FORMATTER.format(birthday)} (\{company})";
        }
    }
}
