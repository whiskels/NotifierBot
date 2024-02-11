package com.whiskels.notifier.reporting.exception;

import com.whiskels.notifier.reporting.ReportType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExceptionEventTest {
    @Test
    @DisplayName("Should initialize exception event via constructor")
    void testRecordCreation() {
        String message = "Test Message";
        ReportType type = EMPLOYEE_EVENT;


        ExceptionEvent event = new ExceptionEvent(message, type);

        assertEquals(message, event.message());
        assertEquals(type, event.type());
    }

    @Test
    @DisplayName("Should initialize exception event via static constructor")
    void testFactoryMethod() {
        String message = "Another Test Message";
        ReportType type = EMPLOYEE_EVENT;

        ExceptionEvent event = ExceptionEvent.of(message, type);

        assertNotNull(event);
        assertEquals(message, event.message());
        assertEquals(type, event.type());
    }

    @Test
    @DisplayName("Should map exception to string")
    void testToString() {
        String message = "Test Message";
        ReportType type = EMPLOYEE_EVENT;

        ExceptionEvent event = new ExceptionEvent(message, type);

        assertTrue(event.toString().contains(message));
        assertTrue(event.toString().contains(type.toString()));
    }

    @Test
    void testEqualsAndHashCode() {
        String message = "Test Message";
        ReportType type = EMPLOYEE_EVENT;

        ExceptionEvent event1 = new ExceptionEvent(message, type);
        ExceptionEvent event2 = new ExceptionEvent(message, type);

        assertEquals(event1, event2);
        assertEquals(event1.hashCode(), event2.hashCode());
    }
}