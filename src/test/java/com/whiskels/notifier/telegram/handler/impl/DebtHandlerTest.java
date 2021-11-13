package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataProvider;
import com.whiskels.notifier.external.json.debt.Debt;
import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Set;

import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_DATE;
import static com.whiskels.notifier.external.json.DebtTestData.debtOne;
import static com.whiskels.notifier.external.json.DebtTestData.debtTwo;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static com.whiskels.notifier.telegram.domain.Role.MANAGER;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Import(DebtHandlerTest.DebtHandlerTestConfig.class)
@SpringBootTest(classes = {DebtHandler.class})
class DebtHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_MANAGER = format("*Overdue debts report on 22-01-2014*%n" +
            "*Test contractor 2*%n" +
            "   Test subj 2%n" +
            "   Test wop 2%n" +
            "   James Bond%n" +
            "   *0 RUB*%n" +
            "No comment%n");


    private static final String EXPECTED_ADMIN = format("%s---------------------------%n" +
            "*Test contractor 1*%n" +
            "   Test subj 1%n" +
            "   Test wop 1%n" +
            "   Jason Bourne%n" +
            "   *0 USD*%n" +
            "No comment%n" +
            "%n", EXPECTED_MANAGER);

    @Autowired
    private DebtHandler debtHandler;

    @Autowired
    private DataProvider<Debt> debtDataProvider;

    @BeforeEach
    public void setHandler() {
        when(debtDataProvider.getData()).thenReturn(List.of(debtOne(), debtTwo()));
        when(debtDataProvider.lastUpdate()).thenReturn(EXPECTED_DATE);
        handler = debtHandler;
    }

    @Test
    void testDebtHandler_authorizedAdmin() {
        testInteraction(USER_1, EXPECTED_ADMIN);
    }

    @Test
    void testDebtHandler_authorizedManager() {
        testInteraction(new User(4, 4, "James Bond", Set.of(MANAGER), emptyList()), format("%s%n", EXPECTED_MANAGER));
    }

    @Test
    void testDebtHandler_Unauthorized() {
        testUnauthorizedInteraction(USER_2);
    }

    @TestConfiguration
    static class DebtHandlerTestConfig {
        @Bean
        DataProvider<Debt> provider() {
            return mock(DataProvider.class);
        }
    }
}
