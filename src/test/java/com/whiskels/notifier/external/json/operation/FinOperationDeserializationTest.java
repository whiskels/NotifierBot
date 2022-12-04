package com.whiskels.notifier.external.json.operation;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.whiskels.notifier.JsonUtils.read;
import static com.whiskels.notifier.TestUtil.assertEqualsIgnoringCR;
import static com.whiskels.notifier.TestUtil.file;
import static com.whiskels.notifier.external.json.operation.FinOperationTestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FinOperationDeserializationTest {
    @Test
    void testFinancialOperationDeserialization() {
        List<FinancialOperation> financialOperations = read(file(OPERATION_JSON), FinancialOperation.class);

        assertEquals(2, financialOperations.size());
        assertEqualsIgnoringCR(operationOpex(), financialOperations.get(0));
        assertEqualsIgnoringCR(operationRevenue(), financialOperations.get(1));
    }
}
