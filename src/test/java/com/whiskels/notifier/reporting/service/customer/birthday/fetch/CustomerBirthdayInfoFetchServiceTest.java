package com.whiskels.notifier.reporting.service.customer.birthday.fetch;

import com.whiskels.notifier.infrastructure.googlesheets.GoogleSheetsReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.whiskels.notifier.JsonUtils.MAPPER;
import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static com.whiskels.notifier.TestUtil.assertEqualsIgnoringCR;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerBirthdayInfoFetchServiceTest {
    private static final String SPREADSHEET = "spreadsheet";
    private static final String CELL_RANGE = "cellRange";

    @Mock
    private GoogleSheetsReader spreadsheetLoader;

    @Test
    @DisplayName("Should load from spreadsheet")
    void shouldLoadFromSpreadSheet() throws Exception {
        final var service =
                new CustomerBirthdayInfoFetchService(CLOCK, spreadsheetLoader, SPREADSHEET, CELL_RANGE);

        when(spreadsheetLoader.read(SPREADSHEET, CELL_RANGE))
                .thenReturn(List.of(
                                List.of("Responsible", "ResponsibleEmail", "ClientId", "Company", "Name", "Surname", "Email", "Telegram", "Phone", "10.02.2024", "Position"),
                                List.of("Filtered", "Filtered", "Filtered", "Filtered", "Filtered", "Filtered", "Filtered", "Filtered", "Filtered", "10.03", "Filtered"),
                                List.of("Exception", "Exception", "Exception", "Exception", "Exception", "Exception", "Exception", "Exception", "Exception", "Exception", "Exception"),
                                List.of("Invalid")
                        )
                );

        final var actual = service.fetch();

        assertEqualsIgnoringCR("""
                {
                  "data" : [ {
                    "responsible" : "Responsible",
                    "responsibleEmail" : "ResponsibleEmail",
                    "clientId" : "ClientId",
                    "company" : "Company",
                    "name" : "Name",
                    "surname" : "Surname",
                    "email" : "Email",
                    "telegram" : "Telegram",
                    "phone" : "Phone",
                    "birthday" : [ 2024, 2, 10 ],
                    "position" : "Position"
                  } ],
                  "requestDate" : [ 2024, 2, 23 ]
                }""", MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(actual));
    }

}