package com.whiskels.notifier.reporting.service.customer.debt.convert;

import com.whiskels.notifier.reporting.service.Report;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.ReportMessageConverter;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.whiskels.notifier.utilities.DateTimeUtil.reportDate;
import static java.util.Collections.singletonList;

@Slf4j
@RequiredArgsConstructor
public class CustomerDebtReportMessageConverter implements ReportMessageConverter<CustomerDebt> {
    private static final int SINGLE_REPORT_CUTOFF = 20;
    private static final Collector<CharSequence, ?, String> COLLECTOR_TWO_NEW_LINES = Collectors.joining(String.format(
            "%n%n"));

    private final String header;
    private final String noData;

    @Override
    @Nonnull
    public Iterable<Report> convert(@Nonnull ReportData<CustomerDebt> data) {
        return data.data().size() < SINGLE_REPORT_CUTOFF
                ? singleReport(data)
                : multiReport(data);
    }

    private List<Report> singleReport(ReportData<CustomerDebt> reportData) {
        return singletonList(
                Report.builder()
                        .header(STR."\{header}\{reportDate(reportData.requestDate())}")
                        .build()
                        .addBody(mapToReportText(reportData.data()))
        );
    }

    private List<Report> multiReport(ReportData<CustomerDebt> reportData) {
        List<Report> reports = new ArrayList<>();
        var content = reportData.data();
        int reportNum = 1;
        for (int i = 0; i < content.size(); i += SINGLE_REPORT_CUTOFF) {
            var subList = content.subList(i, Math.min(content.size(), i + SINGLE_REPORT_CUTOFF));
            reports.add(Report.builder()
                    .header(STR."\{header}\{reportDate(reportData.requestDate())} #\{reportNum++}")
                    .build()
                    .addBody(mapToReportText(subList)));
        }
        return reports;
    }

    private String mapToReportText(List<CustomerDebt> data) {
        if (data.isEmpty()) return noData;
        return data.stream()
                .map(CustomerDebtDto::from)
                .map(CustomerDebtDto::toString)
                .collect(COLLECTOR_TWO_NEW_LINES);
    }
}
