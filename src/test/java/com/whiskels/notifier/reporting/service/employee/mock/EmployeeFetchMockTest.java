package com.whiskels.notifier.reporting.service.employee.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmployeeFetchMockTest {
    @Test
    @DisplayName("Should load mocked data")
    void shouldLoadMockedData() {
        var mock = new EmployeeFetchMock(CLOCK);

        var actual = mock.fetch();

        assertNotNull(actual);
        assertEquals(20, actual.data().size());
    }
}