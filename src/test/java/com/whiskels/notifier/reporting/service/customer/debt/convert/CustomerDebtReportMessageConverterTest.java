package com.whiskels.notifier.reporting.service.customer.debt.convert;

import com.whiskels.notifier.reporting.service.Report;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerDebtReportMessageConverterTest {
    private final String header = "Debt Report on";
    private final String noDataMessage = "No data available";

    private final CustomerDebtReportMessageConverter converter = new CustomerDebtReportMessageConverter(header, noDataMessage);

    @Test
    @DisplayName("Should convert to single report")
    void shouldConvertToSingleReport() {
        // Given
        var expected = Report.builder()
                .header("Debt Report on 23-02-2024")
                .build()
                .addBody("*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment");
        CustomerDebt customerDebt = createCustomerDebt();
        ReportData<CustomerDebt> reportData = new ReportData<>(singletonList(customerDebt), EXPECTED_DATE);

        // When
        var result = (List<Report>) converter.convert(reportData);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected, result.getFirst());
    }

    @Test
    @DisplayName("Should convert to multiple report")
    void shouldConvertToMultipleReports() {
        // Given
        var expectedOne = Report.builder()
                .header("Debt Report on 23-02-2024 #1")
                .build()
                .addBody("*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment\n\n*Test Contractor*\n   Test Finance Subject\n   Test Payment Method\n   Test Manager\n   *1 500 USD*\nTest Comment");


        var expectedTwo = Report.builder()
                .header("Debt Report on 23-02-2024 #2")
                .build()
                .addBody("""
                        *Test Contractor*
                           Test Finance Subject
                           Test Payment Method
                           Test Manager
                           *1 500 USD*
                        Test Comment
                        
                        *Test Contractor*
                           Test Finance Subject
                           Test Payment Method
                           Test Manager
                           *1 500 USD*
                        Test Comment
                        
                        *Test Contractor*
                           Test Finance Subject
                           Test Payment Method
                           Test Manager
                           *1 500 USD*
                        Test Comment
                        
                        *Test Contractor*
                           Test Finance Subject
                           Test Payment Method
                           Test Manager
                           *1 500 USD*
                        Test Comment
                        
                        *Test Contractor*
                           Test Finance Subject
                           Test Payment Method
                           Test Manager
                           *1 500 USD*
                        Test Comment""");

        List<CustomerDebt> customerDebts = Collections.nCopies(25, createCustomerDebt());
        ReportData<CustomerDebt> reportData = new ReportData<>(customerDebts, EXPECTED_DATE);

        // When
        var result = (List<Report>) converter.convert(reportData);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedOne, result.getFirst());
        assertEquals(expectedTwo, result.get(1));
    }

    @Test
    @DisplayName("Should convert when data is empty")
    void shouldConvertWhenDataIsEmpty() {
        // Given
        var expected = Report.builder()
                .header("Debt Report on 23-02-2024")
                .build()
                .addBody("No data available");

        ReportData<CustomerDebt> reportData = new ReportData<>(emptyList(), EXPECTED_DATE);

        // When
        var result = (List<Report>) converter.convert(reportData);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected, result.getFirst());
    }

    private CustomerDebt createCustomerDebt() {
        CustomerDebt customerDebt = new CustomerDebt();
        customerDebt.setContractor("Test Contractor");
        customerDebt.setFinanceSubject("Test Finance Subject");
        customerDebt.setWayOfPayment("Test Payment Method");
        customerDebt.setAccountManager("Test Manager");
        customerDebt.setCurrency("USD");
        customerDebt.setComment("Test Comment");
        customerDebt.setDelay0(BigDecimal.valueOf(100));
        customerDebt.setDelay30(BigDecimal.valueOf(200));
        customerDebt.setDelay60(BigDecimal.valueOf(300));
        customerDebt.setDelay90(BigDecimal.valueOf(400));
        customerDebt.setDelay180(BigDecimal.valueOf(500));
        customerDebt.calculateTotal();
        var currencyRate = new CurrencyRate();
        currencyRate.setRubRates(Map.of("USD", BigDecimal.valueOf(75)));
        customerDebt.calculateTotalRouble(currencyRate);
        return customerDebt;
    }
}