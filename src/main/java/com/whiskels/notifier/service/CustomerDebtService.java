package com.whiskels.notifier.service;

import com.whiskels.notifier.model.CustomerDebt;
import com.whiskels.notifier.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.whiskels.notifier.util.DateTimeUtil.getWithOffset;
import static com.whiskels.notifier.util.FormatUtil.formatListWithEmptyLine;
import static com.whiskels.notifier.util.FormatUtil.reportHeader;
import static com.whiskels.notifier.util.StreamUtil.alwaysTruePredicate;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerDebtService extends AbstractJSONService {
    private static final int MIN_RUB_VALUE = 500;
    private static final String DEBT_REPORT_HEADER = "Overdue debts";

    @Value("${json.customer.debt.url}")
    private String customerUrl;

    @Value("${moex.url}")
    private String moexUrl;

    @Value("${moex.usd}")
    private String moexUsd;

    @Value("${moex.eur}")
    private String moexEur;

    private double usdRate;
    private double eurRate;

    private List<CustomerDebt> customerDebts;

    @PostConstruct
    private void initCustomerList() {
        update();
    }

    @Scheduled(cron = "${json.customer.debt.cron}")
    protected void update() {
        updateExchangeRates();
        updateCustomerList();
    }


    public String dailyMessage(Predicate<CustomerDebt> predicate) {
        log.debug("Preparing customer debts message");

        return reportHeader(DEBT_REPORT_HEADER, getWithOffset(serverHourOffset)) +
                formatListWithEmptyLine(customerDebts, predicate);
    }

    public String dailyMessage() {
        return dailyMessage(alwaysTruePredicate());
    }

    /**
     * Updates USD/RUB and EUR/RUB exchange rates using MOEX data
     */
    private void updateExchangeRates() {
        log.info("updating exchange rates");
        // Getting moex exchange rates string
        String moexContent = null;
        try {
            moexContent = IOUtils.toString(new URL(moexUrl), StandardCharsets.UTF_8);
            // Removing HTML tags
            moexContent = moexContent.replaceAll("\\<.*?\\>", "");

            // Converting data to string array
            String[] moexArray = moexContent.split("\n");

            // Mapping data
            final int moexDataLength = (moexArray.length - 1) / 2;
            HashMap<String, String> ratesMap = new HashMap<>(moexDataLength);
            for (int i = 0; i < moexDataLength; i++) {
                ratesMap.put(moexArray[i + 1], moexArray[i + 1 + moexDataLength]);
            }

            if (!ratesMap.isEmpty()) {
                usdRate = Double.parseDouble(ratesMap.get(moexUsd));
                eurRate = Double.parseDouble(ratesMap.get(moexEur));
            }
        } catch (IOException e) {
            log.error("Exception while trying to get MOEX data: {}", e.toString());
        } catch (NumberFormatException e) {
            log.error("Exception while trying to update exchange rate: {}", e.toString());
        }
    }

    /**
     * Reads JSON data from URL and creates Customer list
     */
    private void updateCustomerList() {
        log.info("updating customer debt list");
        customerDebts = filterByMinRubValue(readFromJson(customerUrl));
    }

    private List<CustomerDebt> readFromJson(String url) {
        return JsonUtil.readValuesFromNode(url, CustomerDebt.class, "content");
    }

    private List<CustomerDebt> filterByMinRubValue(List<CustomerDebt> customerDebtList) {
        customerDebtList.forEach(customerDebt -> customerDebt.calculateOverallDebt(usdRate, eurRate));

        return customerDebtList.stream()
                .filter(debtHigherThenMinRub())
                .sorted()
                .collect(Collectors.toList());
    }

    private Predicate<CustomerDebt> debtHigherThenMinRub() {
        return customerDebt -> customerDebt.getTotalDebtRouble() > MIN_RUB_VALUE;
    }
}
