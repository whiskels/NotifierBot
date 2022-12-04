package com.whiskels.notifier.external.json.employee;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.whiskels.notifier.external.json.employee._EmployeeBeanConfig.EMPLOYEE_URL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@FeignClient(name = "employeeClient", url = "${" + EMPLOYEE_URL + "}")
@ConditionalOnProperty(EMPLOYEE_URL)
interface EmployeeFeignClient {
    @RequestMapping(method = GET)
    List<Employee> get();
}
