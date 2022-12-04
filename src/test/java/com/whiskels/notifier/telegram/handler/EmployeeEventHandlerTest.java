package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.external.ReportData;
import com.whiskels.notifier.external.ReportSupplier;
import com.whiskels.notifier.external.json.employee.EmployeeDto;
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
import static com.whiskels.notifier.external.json.employee.EmployeeTestData.employeeNullBirthday;
import static com.whiskels.notifier.external.json.employee.EmployeeTestData.employeeWorking;
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

    @BeforeEach
    public void setHandler() {
        handler = employeeEventHandler;
    }

    @Test
    void testEmployeeEventHandler_authorized() {


        testInteraction(USER_1, EXPECTED_AUTH);
    }

    @Test
    void testHelpHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2);
    }

    @TestConfiguration
    static class EmployeeEventHandlerTestConfig {
        @Bean
        ReportSupplier<EmployeeDto> reportSupplier() {
            var mock = mock(ReportSupplier.class);
            when(mock.get()).thenReturn(new ReportData<>(
                    List.of(EmployeeDto.from(employeeWorking()), EmployeeDto.from(employeeNullBirthday())),
                    EXPECTED_DATE
            ));

            return mock;
        }
    }
}
