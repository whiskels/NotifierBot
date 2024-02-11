package com.whiskels.notifier.reporting.service.employee.fetch;

import com.whiskels.notifier.infrastructure.config.feign.FeignProxyConfig;
import com.whiskels.notifier.reporting.service.employee.domain.Employee;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import static com.whiskels.notifier.reporting.service.employee.config.EmployeeEventFetchConfig.EMPLOYEE_URL;
import static org.springframework.web.bind.annotation.RequestMethod.GET;


@ConditionalOnProperty(EMPLOYEE_URL)
@FeignClient(name = "employeeClient", url = "${" + EMPLOYEE_URL + "}", configuration = FeignProxyConfig.class)
public interface EmployeeFeignClient {

    @RequestMapping(method = GET)
    List<Employee> get();
}
