package com.whiskels.notifier.reporting.service.customer.debt.convert;

import com.slack.api.webhook.Payload;
import com.whiskels.notifier.reporting.service.ReportData;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CurrencyRate;
import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static com.whiskels.notifier.JsonUtils.assertEqualsWithJson;
import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;

class CustomerDebtReportMessageConverterTest {
    private final String header = "Debt Report on";
    private final String noDataMessage = "No data available";

    private final CustomerDebtReportMessageConverter converter = new CustomerDebtReportMessageConverter(header, noDataMessage);

    @Test
    @DisplayName("Should convert to single report")
    void shouldConvertToSingleReport() throws Exception {
        // Given
        String expected = """
                {
                  "threadTs" : null,
                  "text" : "Debt Report on 23-02-2024",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Debt Report on 23-02-2024",
                      "emoji" : false
                    }
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "@channel",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        CustomerDebt customerDebt = createCustomerDebt();
        ReportData<CustomerDebt> reportData = new ReportData<>(singletonList(customerDebt), EXPECTED_DATE);

        // When
        var result = (List<Payload>) converter.convert(reportData);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result.getFirst()));
    }

    @Test
    @DisplayName("Should convert to multiple report")
    void shouldConvertToMultipleReports() throws Exception{
        // Given
        String expectedOne = """
                {
                  "threadTs" : null,
                  "text" : "Debt Report on 23-02-2024 #1",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Debt Report on 23-02-2024 #1",
                      "emoji" : false
                    }
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "@channel",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        String expectedTwo = """
                {
                  "threadTs" : null,
                  "text" : "Debt Report on 23-02-2024 #2",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Debt Report on 23-02-2024 #2",
                      "emoji" : false
                    }
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "@channel",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment\\n\\n*Test Contractor*\\n   Test Finance Subject\\n   Test Payment Method\\n   Test Manager\\n   *1 500 USD*\\nTest Comment",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        List<CustomerDebt> customerDebts = Collections.nCopies(25, createCustomerDebt());
        ReportData<CustomerDebt> reportData = new ReportData<>(customerDebts, EXPECTED_DATE);

        // When
        var result = (List<Payload>) converter.convert(reportData);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedOne, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result.getFirst()));
        assertEquals(expectedTwo, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result.get(1)));
    }

    @Test
    @DisplayName("Should convert when data is empty")
    void shouldConvertWhenDataIsEmpty() throws Exception {
        // Given
        String expected = """
                {
                  "threadTs" : null,
                  "text" : "Debt Report on 23-02-2024",
                  "channel" : null,
                  "username" : null,
                  "iconUrl" : null,
                  "iconEmoji" : null,
                  "blocks" : [ {
                    "type" : "header",
                    "blockId" : "0",
                    "text" : {
                      "type" : "plain_text",
                      "text" : "Debt Report on 23-02-2024",
                      "emoji" : false
                    }
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "@channel",
                      "verbatim" : null
                    },
                    "blockId" : "1",
                    "fields" : null,
                    "accessory" : null
                  }, {
                    "type" : "section",
                    "text" : {
                      "type" : "mrkdwn",
                      "text" : "No data available",
                      "verbatim" : null
                    },
                    "blockId" : "2",
                    "fields" : null,
                    "accessory" : null
                  } ],
                  "attachments" : null,
                  "unfurlLinks" : null,
                  "unfurlMedia" : null,
                  "metadata" : null
                }""";
        ReportData<CustomerDebt> reportData = new ReportData<>(emptyList(), EXPECTED_DATE);

        // When
        var result = (List<Payload>) converter.convert(reportData);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected, MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(result.getFirst()));
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