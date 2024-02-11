package com.whiskels.notifier.reporting.service.customer.debt.convert;

import com.whiskels.notifier.reporting.service.customer.debt.domain.CustomerDebt;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.whiskels.notifier.TestUtil.assertEqualsIgnoringCR;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CustomerDebtDtoTest {

    @Test
    @DisplayName("Should map from customer")
    void shouldMapFromCustomer() {
        CustomerDebt customerDebt = new CustomerDebt();
        customerDebt.setContractor("Contractor");
        customerDebt.setAccountManager("Account Manager");
        customerDebt.setFinanceSubject("Finance Subject");
        customerDebt.setWayOfPayment("Way Of Payment");
        customerDebt.setCurrency("USD");
        customerDebt.setComment("Comment");
        customerDebt.setTotal(BigDecimal.valueOf(1000));

        CustomerDebtDto dto = CustomerDebtDto.from(customerDebt);

        assertNotNull(dto);
        assertEqualsIgnoringCR("""
                *Contractor*
                   Finance Subject
                   Way Of Payment
                   Account Manager
                   *1 000 USD*
                Comment""", dto.toString());

    }
}