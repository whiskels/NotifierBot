package com.whiskels.notifier.external.json;

import com.whiskels.notifier.external.debt.domain.Debt;
import com.whiskels.notifier.external.employee.domain.Employee;
import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.whiskels.notifier.AssertUtil.assertEqualsIgnoringCR;
import static com.whiskels.notifier.external.DebtTestData.*;
import static com.whiskels.notifier.external.EmployeeTestData.*;
import static com.whiskels.notifier.external.FinOperationTestData.*;
import static com.whiskels.notifier.telegram.UpdateTestData.HELP_MESSAGE_JSON;
import static org.junit.jupiter.api.Assertions.*;

class JsonReaderTest {
    private final JsonReader reader = new JsonReader();

    @Test
    void testDebtDeserialization() {
        List<Debt> debts = reader.read(
                file(DEBT_JSON),
                "content",
                Debt.class
        );

        assertEquals(2, debts.size());
        assertEqualsIgnoringCR(debtOne(), debts.get(0));
        assertEqualsIgnoringCR(debtTwo(), debts.get(1));
    }

    @Test
    void testUpdateDeserialization() {
        Update actual = reader.read(
                file(HELP_MESSAGE_JSON),
                Update.class)
                .get(0);

        assertNotNull(actual);
        assertTrue(actual.hasMessage());
        Message message = actual.getMessage();
        assertEquals(Integer.valueOf(1), message.getFrom().getId());
        assertEquals("/help", message.getText());
    }

    @Test
    void testFinancialOperationDeserialization() {
        List<FinancialOperation> financialOperations = reader.read(
                file(OPERATION_JSON),
                FinancialOperation.class
        );

        assertEquals(2, financialOperations.size());
        assertEqualsIgnoringCR(operationOpex(), financialOperations.get(0));
        assertEqualsIgnoringCR(operationRevenue(), financialOperations.get(1));
    }

    @Test
    void testEmployeeDeserialization() {
        List<Employee> employees = reader.read(
                file(EMPLOYEE_JSON),
                Employee.class
        );

        assertEquals(3, employees.size());
        assertEqualsIgnoringCR(employeeWorking(), employees.get(0));
        assertEqualsIgnoringCR(employeeDecree(), employees.get(1));
        assertEqualsIgnoringCR(employeeFired(), employees.get(2));
    }

    @SneakyThrows
    private String file(String path) {
        return new ClassPathResource(path).getURL().toString();
    }
}
