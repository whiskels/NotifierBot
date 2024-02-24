package com.whiskels.notifier.reporting.service.customer.debt.fetch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDebtDebtDataFetchServiceTest {
    private static final String DEBT_TEST_JSON = """
            {
              "status": 1,
              "content": [
                {
                  "c2fs_id": 123,
                  "wop_id": 1234,
                  "contractor": "Test contractor 1",
                  "finance_subject": "Test subj 1",
                  "wop": "Test wop 1",
                  "account_manager": "Jason Bourne",
                  "currency": "USD",
                  "debt_comment": null,
                  "debtor_delay_180": "50000",
                  "debtor_delay_90": "50000",
                  "debtor_delay_60": "50000",
                  "debtor_delay_30": "50000",
                  "debtor_delay_0": "50000",
                  "debtor_delay_current": "0",
                  "not_realization": "0",
                  "delay_90": "0",
                  "delay_60": "0",
                  "delay_30": "0"
                },
                {
                  "c2fs_id": 123,
                  "wop_id": 1235,
                  "contractor": "Test contractor 2",
                  "finance_subject": "Test subj 2",
                  "wop": "Test wop 2",
                  "account_manager": "James Bond",
                  "currency": "RUB",
                  "debt_comment": null,
                  "debtor_delay_180": "0",
                  "debtor_delay_90": "0",
                  "debtor_delay_60": "0",
                  "debtor_delay_30": "0",
                  "debtor_delay_0": "0",
                  "debtor_delay_current": "0",
                  "not_realization": "-413826",
                  "delay_90": "413826",
                  "delay_60": "413826",
                  "delay_30": "413826"
                }
              ],
              "message": null
            }
            """;

    private static final String DEBT_EXPECTED_JSON = """
            {
              "data" : [ {
                "contractor" : "Test contractor 1",
                "currency" : "USD",
                "total" : 250000,
                "totalRouble" : 23825834,
                "finance_subject" : "Test subj 1",
                "wop" : "Test wop 1",
                "account_manager" : "Jason Bourne",
                "debt_comment" : null,
                "debtor_delay_180" : 50000,
                "debtor_delay_90" : 50000,
                "debtor_delay_60" : 50000,
                "debtor_delay_30" : 50000,
                "debtor_delay_0" : 50000,
                "debtor_delay_current" : "0"
              } ],
              "requestDate" : [ 2024, 2, 23 ]
            }""";

    @Mock
    private DataFetchService<CurrencyRate> rateReportSupplier;
    @Mock
    private CustomerDebtFeignClient debtClient;

    @Test
    @DisplayName("Should fetch customer debt report")
    void testFetchData() throws Exception {
        final var currencyRate = new CurrencyRate();
        currencyRate.setRubRates(Map.of("usd", new BigDecimal("0.010492812"), "eur", new BigDecimal("0.0098389599")));
        when(rateReportSupplier.fetch()).thenReturn(new ReportData<>(Collections.singletonList(currencyRate), null));
        final var debtData = MAPPER.readValue(DEBT_TEST_JSON, CustomerDebtData.class);
        when(debtClient.get()).thenReturn(debtData);
        final var service = new CustomerDebtDebtDataFetchService(rateReportSupplier, debtClient, MockedClockConfiguration.CLOCK);

        final var actual = service.fetch();

        assertEquals(MAPPER.readValue(DEBT_EXPECTED_JSON, new TypeReference<ReportData<CustomerDebt>>() {
        }), actual);
    }
}