package com.whiskels.notifier.external;

import com.whiskels.notifier.external.operation.domain.FinancialOperation;
import lombok.experimental.UtilityClass;

import java.util.GregorianCalendar;

import static java.util.Calendar.JANUARY;

@UtilityClass
public class FinOperationTestData {
    public static final String OPERATION_JSON = "json/external/finoperation.json";

    public static FinancialOperation operationOpex() {
        FinancialOperation op = new FinancialOperation();
        op.setCrmId(123);
        op.setDate(new GregorianCalendar(2020, JANUARY, 5, 3, 0).getTime());
        op.setCurrency("USD");
        op.setAmount(-38.55d);
        op.setAmountUsd(-38.55d);
        op.setAmountRub(-2386.46);
        op.setBank("TestBank");
        op.setBankAccount("1234xxxxxxxx5678");
        op.setLegalName("Company name");
        op.setType("supplier");
        op.setContractorLegalName("Test contractor 1 LLC");
        op.setContractor("Test contractor 1");
        op.setCategory("OPEX");
        op.setSubcategory("Software Trackers");
        op.setProject("Creative");
        op.setOffice("Moscow");
        op.setDescription("Test contractor 1 LLC costs");
        return op;
    }

    public static FinancialOperation operationRevenue() {
        FinancialOperation op = new FinancialOperation();
        op.setCrmId(124);
        op.setDate(new GregorianCalendar(2020, JANUARY, 5, 3, 0).getTime());
        op.setCurrency("USD");
        op.setAmount(200d);
        op.setAmountUsd(200d);
        op.setAmountRub(12381.14);
        op.setBank("TestBank");
        op.setBankAccount("1234xxxxxxxx5678");
        op.setLegalName("Company name");
        op.setType("supplier");
        op.setContractorLegalName("Test contractor 2 LLC");
        op.setContractor("Test contractor 2");
        op.setCategory("Revenue");
        op.setSubcategory("Revenue");
        op.setProject("Admin");
        op.setOffice("Moscow");
        op.setDescription("Test contractor 2 LLC Revenue");
        return op;
    }
}
