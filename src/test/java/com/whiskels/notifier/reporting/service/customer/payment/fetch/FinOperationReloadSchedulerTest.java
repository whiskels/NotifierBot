package com.whiskels.notifier.reporting.service.customer.payment.fetch;

import com.whiskels.notifier.reporting.service.DataFetchService;
import com.whiskels.notifier.reporting.service.customer.payment.domain.FinancialOperation;
import com.whiskels.notifier.reporting.service.customer.payment.fetch.FinOperationReloadScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class FinOperationReloadSchedulerTest {

    @Mock
    private DataFetchService<FinancialOperation> dataFetchService;

    @InjectMocks
    private FinOperationReloadScheduler finOperationReloadScheduler;

    @Test
    @DisplayName("Should execute scheduled reload")
    public void shouldExecuteScheduledReload() {
        finOperationReloadScheduler.loadScheduled();

        verify(dataFetchService).fetch();
    }
}