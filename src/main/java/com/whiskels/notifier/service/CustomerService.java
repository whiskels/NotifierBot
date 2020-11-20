package com.whiskels.notifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whiskels.notifier.model.Customer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
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

    @Getter
    private List<Customer> customerList;

    private final JSONReader jsonReader;

    @PostConstruct
    private void initCustomerList() {
        update();
    }

    /**
     * Reads JSON data from URL and creates Customer list
     */
    @Scheduled(cron = "${json.customer.cron}")
    public void update() {
        log.info("updating exchange rates");
        updateExchangeRates();
        log.info("updating customer list");
        JSONObject json = (JSONObject) jsonReader.readJsonFromUrl(customerUrl);
        if (json != null) {
            createCustomerList(json);
            log.info("customer list updated");
        }
    }

    /**
     * Updates USD/RUB and EUR/RUB exchange rates using MOEX data
     */
    private void updateExchangeRates() {
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
     * Creates customer list based on JSONArray of objects
     */
    private void createCustomerList(JSONObject json) {
        customerList = new ArrayList<>();
        JSONArray content = (JSONArray) json.get("content");

        try {
            for (Object o : content) {
                StringReader reader = new StringReader(o.toString());

                Customer customer = new ObjectMapper().readValue(reader, Customer.class);
                customer.calculateOverallDebt(usdRate, eurRate);

                customerList.add(customer);
            }
        } catch (IOException e) {
            log.error("Exception while reading value from reader - {}", e.getMessage());
        }

        customerList = customerList.stream()
                .filter(customer -> customer.getTotalDebtRouble() > 500)
                .sorted(Comparator.comparingDouble(Customer::getTotalDebtRouble).thenComparing(Customer::getContractor).reversed())
                .collect(Collectors.toList());
    }

}
