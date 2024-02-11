package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.FinOperationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MockedClockConfiguration.class)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "report.parameters.customer-payment.url=test")
@ActiveProfiles("test-containers")
class FinOperationRepositoryTest {

    @Autowired
    private FinOperationRepository finOperationRepository;

    @Autowired
    private Clock clock;

    @Test
    @DisplayName("Should get present CRM ids")
    public void shouldGetPresentCrmIds() {
        FinancialOperation operation1 = new FinancialOperation();
        operation1.setCrmId(1);
        FinancialOperation operation2 = new FinancialOperation();
        operation2.setCrmId(2);
        finOperationRepository.saveAll(List.of(operation1, operation2));

        Set<Integer> crmIds = finOperationRepository.getPresentCrmIds();

        assertThat(crmIds).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("Should get all by category and date between")
    void shouldGetAllByCategoryAndDateBetween() {
        final var category = "Test";
        FinancialOperation operation1 = new FinancialOperation();
        operation1.setCategory(category);
        finOperationRepository.save(operation1);

        LocalDateTime startDate = LocalDateTime.now(clock).with(LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.now(clock).with(LocalTime.MAX);
        List<FinancialOperation> results = finOperationRepository.getAllByCategoryAndDateBetween(category, startDate, endDate);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getCategory()).isEqualTo(category);
    }
}