package com.whiskels.notifier.reporting.service;


import java.time.LocalDate;
import java.util.List;

public record ReportData<T>(List<T> data, LocalDate requestDate) {
}
