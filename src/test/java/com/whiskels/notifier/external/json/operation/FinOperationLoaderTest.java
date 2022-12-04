package com.whiskels.notifier.external.json.operation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.CLOCK;
import static com.whiskels.notifier.external.json.operation.FinOperationTestData.operationOpex;
import static com.whiskels.notifier.external.json.operation.FinOperationTestData.operationRevenue;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinOperationLoaderTest {

    @Mock
    private FinOperationFeignClient finOperationFeignClient;

    @Mock
    private FinOperationRepository finOperationRepository;

    private FinOperationLoader loader;

    @Test
    void testFinOperationDataLoader() {
        loader = new FinOperationLoader(finOperationRepository, CLOCK, finOperationFeignClient);

        when(finOperationFeignClient.get()).thenReturn(List.of(operationOpex(), operationRevenue()));
        when(finOperationRepository.getPresentCrmIds()).thenReturn(singleton(123));
        when(finOperationRepository.saveAll(any())).thenReturn(List.of(operationRevenue()));

        var actual = loader.load();

        verify(finOperationFeignClient).get();
        verifyNoMoreInteractions(finOperationFeignClient);
        verify(finOperationRepository).getPresentCrmIds();
        verify(finOperationRepository).saveAll(any());
        verifyNoMoreInteractions(finOperationRepository);

        assertThat(actual).usingRecursiveFieldByFieldElementComparator().containsOnly(operationRevenue());
    }

    @Test
    void testFinOperationDataLoaderOnWeekend() {
        loader = new FinOperationLoader(
                finOperationRepository,
                Clock.fixed(Instant.parse("2022-12-04T10:15:30Z"), ZoneId.of("UTC")),
                finOperationFeignClient
        );


        var actual = loader.load();

        verifyNoInteractions(finOperationFeignClient);
        verifyNoInteractions(finOperationRepository);

        assertThat(actual).isEmpty();
    }
}
