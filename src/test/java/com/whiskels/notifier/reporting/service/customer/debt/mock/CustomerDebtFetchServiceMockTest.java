package com.whiskels.notifier.reporting.service.customer.debt.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static org.junit.jupiter.api.Assertions.*;

class CustomerDebtFetchServiceMockTest {

    @Test
    @DisplayName("Should load mocked data")
    void shouldLoadMockedData() {
        var mock = new CustomerDebtFetchServiceMock(CLOCK);

        var actual = mock.fetch();

        assertNotNull(actual);
        assertEquals(13, actual.data().size());
    }
}