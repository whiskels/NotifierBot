package com.whiskels.notifier.external.json.employee;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.whiskels.notifier.external.json.employee.EmployeeTestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeLoaderTest {

    @Mock
    private EmployeeFeignClient employeeFeignClient;

    @InjectMocks
    private EmployeeLoader loader;

    @Test
    void testEmployeeDataLoader() {
        when(employeeFeignClient.get()).thenReturn(List.of(employeeFired(), employeeWorking(), employeeDecree()));

        var actual = loader.load();

        assertThat(actual).containsOnly(employeeWorking());
    }
}
