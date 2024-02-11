package com.whiskels.notifier.reporting.service.customer.birthday.mock;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static org.junit.jupiter.api.Assertions.*;

class CustomerBirthdayInfoFetchServiceMockTest {

    @Test
    @DisplayName("Should load mocked data")
    void shouldLoadMockedData() {
        var mock = new CustomerBirthdayInfoFetchServiceMock(CLOCK);

        var actual = mock.fetch();

        assertNotNull(actual);
        assertEquals(17, actual.data().size());
    }
}