package com.whiskels.telegrambot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whiskels.telegrambot.model.Employee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {

    @Value("${json.employee.url}")
    private String employeeUrl;

    @Getter
    private List<Employee> employeeList;

    private final JSONReader jsonReader;

    @PostConstruct
    private void initEmployeeList() {
        update();
    }

    /**
     * Reads JSON data from URL and creates Customer list
     */
    @Scheduled(cron = "${json.employee.cron}")
    public void update() {
        log.info("updating employee list");
        JSONArray json = (JSONArray) jsonReader.readJsonFromUrl(employeeUrl);
        if (json != null) {
            createEmployeeList(json);
            log.info("employee list updated");
        }
    }

    /**
     * Creates customer list based on JSONArray of objects
     */
    private void createEmployeeList(JSONArray json) {
        employeeList = new ArrayList<>();
        try {
            for (Object o : json) {
                final StringReader reader = new StringReader(o.toString());

                Employee employee = new ObjectMapper().readValue(reader, Employee.class);

                if (!employee.getStatusSystem().equalsIgnoreCase(Employee.STATUS_SYSTEM_FIRED) &&
                        !employee.getStatus().equalsIgnoreCase(Employee.STATUS_DECREE)) {
                    log.debug(employee.toString());


                    employeeList.add(employee);
                }
            }
        } catch (IOException e) {
            log.error("Exception while reading value from reader - {}", e.getMessage());
        }
    }
}