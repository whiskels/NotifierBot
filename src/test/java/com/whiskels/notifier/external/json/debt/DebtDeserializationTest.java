package com.whiskels.notifier.external.json.debt;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.whiskels.notifier.JsonUtils.read;
import static com.whiskels.notifier.TestUtil.file;
import static com.whiskels.notifier.external.json.debt.DebtTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DebtDeserializationTest {
    @Test
    void testDebtDeserialization() {
        List<Debt> debts = read(file(DEBT_JSON), "content", Debt.class);

        assertEquals(2, debts.size());
        assertEquals(debtOne(), debts.get(0));
        assertEquals(debtTwo(), debts.get(1));
    }
}
