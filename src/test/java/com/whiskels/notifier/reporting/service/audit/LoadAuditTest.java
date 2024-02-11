package com.whiskels.notifier.reporting.service.audit;

import com.whiskels.notifier.reporting.ReportType;
import com.whiskels.notifier.reporting.service.audit.LoadAudit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.whiskels.notifier.reporting.ReportType.EMPLOYEE_EVENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoadAuditTest {

    @Test
    @DisplayName("Should initialize load audit via static constructor")
    void testLoadAudit() {
        LoadAudit loadAudit = LoadAudit.loadAudit(5, EMPLOYEE_EVENT);
        assertEquals(5, loadAudit.getCount());
        assertEquals(EMPLOYEE_EVENT, loadAudit.getReportType());
    }

    @Test
    @DisplayName("Should use hibernate equals")
    void testEquals() {
        LoadAudit loadAudit1 = LoadAudit.loadAudit(5, EMPLOYEE_EVENT);
        loadAudit1.setId(1);
        LoadAudit loadAudit2 = LoadAudit.loadAudit(5, EMPLOYEE_EVENT);
        loadAudit2.setId(1);
        assertEquals(loadAudit1, loadAudit2);
        assertNotEquals(loadAudit1, new Object());
    }

    @Test
    @DisplayName("Should have equal hash codes")
    void testHashCode() {
        LoadAudit loadAudit1 = LoadAudit.loadAudit(5, EMPLOYEE_EVENT);
        LoadAudit loadAudit2 = LoadAudit.loadAudit(5, EMPLOYEE_EVENT);
        assertEquals(loadAudit1.hashCode(), loadAudit2.hashCode());
    }
}