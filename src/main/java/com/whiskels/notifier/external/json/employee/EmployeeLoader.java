package com.whiskels.notifier.external.json.employee;

import com.whiskels.notifier.external.json.JsonLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

import static com.whiskels.notifier.common.datetime.DateTimeUtil.birthdayComparator;
import static com.whiskels.notifier.common.util.StreamUtil.filterAndSort;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.NOT_DECREE;
import static com.whiskels.notifier.external.json.employee.EmployeeUtil.NOT_FIRED;

@Component
@ConditionalOnProperty("external.employee.url")
public class EmployeeLoader extends JsonLoader<Employee> {
    @SuppressWarnings("unchecked")
    private static final Predicate<Employee>[] EMPLOYEE_FILTERS = new Predicate[]{NOT_DECREE, NOT_FIRED};

    public EmployeeLoader(@Value("${external.employee.url}") String jsonUrl) {
        super(jsonUrl);
    }

    @Override
    public List<Employee> load() {
        return filterAndSort(loadFromJson(), birthdayComparator(), EMPLOYEE_FILTERS);
    }
}
