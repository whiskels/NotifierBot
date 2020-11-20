package com.whiskels.notifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whiskels.notifier.model.Customer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
    @Value("${json.customer.url}")
    private String customerUrl;

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
        log.info("updating customer list");
        JSONObject json = (JSONObject) jsonReader.readJsonFromUrl(customerUrl);
        if (json != null) {
            createCustomerList(json);
            log.info("customer list updated");
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
                customer.calculateOverallDebt();

                customerList.add(customer);
            }
        } catch (IOException e) {
            log.error("Exception while reading value from reader - {}", e.getMessage());
        }

        customerList = customerList.stream()
                .filter(customer -> customer.getOverallDebt() > 10)
                .sorted(Comparator.comparingDouble(Customer::getOverallDebt).reversed())
                .collect(Collectors.toList());
    }

}
