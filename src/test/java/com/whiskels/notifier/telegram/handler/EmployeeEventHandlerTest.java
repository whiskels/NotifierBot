package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.Supplier;
import com.whiskels.notifier.external.json.employee.Employee;
import com.whiskels.notifier.telegram.CommandHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static com.whiskels.notifier.external.json.EmployeeTestData.employeeNullBirthday;
import static com.whiskels.notifier.external.json.EmployeeTestData.employeeWorking;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Import(EmployeeEventHandlerTest.EmployeeEventHandlerTestConfig.class)
@SpringBootTest(classes = {EmployeeEventHandler.class})
class EmployeeEventHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_AUTH = format("*Employee events on 22-01-2014*%n%n*Birthdays:*%nJason Bourne 01.01%n%n*Work anniversaries:*%nNobody");

    @Autowired
    private CommandHandler employeeEventHandler;

    @Autowired
    private Supplier<Employee> employeeSupplier;

    @BeforeEach
    public void setHandler() {
        handler = employeeEventHandler;
    }

    @Test
    void testEmployeeEventHandler_authorized() {
        when(employeeSupplier.getData()).thenReturn(List.of(employeeWorking(), employeeNullBirthday()));
        when(employeeSupplier.lastUpdate()).thenReturn(EXPECTED_DATE);

        testInteraction(USER_1, EXPECTED_AUTH);
    }

    @Test
    void testHelpHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2);
    }

    @TestConfiguration
    static class EmployeeEventHandlerTestConfig {
        @Bean
        Supplier<Employee> provider() {
            return mock(Supplier.class);
        }
    }
}
