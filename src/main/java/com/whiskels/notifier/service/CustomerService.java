package com.whiskels.notifier.service;

import com.whiskels.notifier.model.Customer;
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
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.whiskels.notifier.util.FormatUtil.DATE_YEAR_FORMATTER;
import static com.whiskels.notifier.util.FormatUtil.EMPTY_LINE;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService extends AbstractJSONService {
    @Value("${json.customer.url}")
    private String customerUrl;

    @Value("${moex.url}")
    private String moexUrl;

    @Value("${moex.usd}")
    private String moexUsd;

    @Value("${moex.eur}")
    private String moexEur;

    private double usdRate;
    private double eurRate;

    private List<Customer> customerList;

    @PostConstruct
    private void initCustomerList() {
        update();
    }

    /**
     * Reads JSON data from URL and creates Customer list
     */
    @Scheduled(cron = "${json.customer.cron}")
    public void update() {
        updateExchangeRates();
        updateCustomerList();
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

    private void updateCustomerList() {
        log.info("updating customer list");
        customerList = JsonUtil.readValuesFromNode(customerUrl, Customer.class, "content");

        customerList
                .forEach(customer -> customer.calculateOverallDebt(usdRate, eurRate));

        customerList = customerList.stream()
                .filter(customer -> customer.getTotalDebtRouble() > 500)
                .sorted(Comparator.comparingDouble(Customer::getTotalDebtRouble)
                        .thenComparing(Customer::getContractor).reversed())
                .collect(Collectors.toList());
    }

    public String createCustomerDebtMessage(Predicate<Customer> predicate) {
        log.debug("Preparing customer debts message for Slack");

        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("Overdue debts on %s%n",
                DATE_YEAR_FORMATTER.format(LocalDateTime.now().plusHours(serverHourOffset))));
        String customerInfo = "";
        try {
            customerInfo = customerList.stream()
                    .filter(predicate)
                    .map(Customer::toString)
                    .collect(Collectors.joining(String.format(
                            "%n%s%n", EMPTY_LINE)));

        } catch (Exception e) {
            log.error("Exception while creating message GET: {}", e.getMessage());
        }

        sb.append(customerInfo.isEmpty() ? "No overdue debts" : customerInfo);

        return sb.toString();
    }

    public static Predicate<Customer> alwaysTruePredicate() {
        return customer -> true;
    }
}
