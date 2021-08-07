package com.whiskels.notifier.telegram;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.telegram.orchestrator.SchedulableHandlerOrchestrator;
import com.whiskels.notifier.telegram.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.util.List;

import static com.whiskels.notifier.MockedClockConfiguration.EXPECTED_TIME;
import static com.whiskels.notifier.telegram.ScheduleTestData.SCHEDULE_USER_1_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = MessageSchedulerTest.MessageSchedulerTestConfig.class)
class MessageSchedulerTest {
    @Autowired
    private MessageScheduler scheduler;

    @Autowired
    private ScheduleService service;

    @Autowired
    private SchedulableHandlerOrchestrator orchestrator;

    @BeforeEach
    void resetMocks() {
        reset(service);
        reset(orchestrator);
    }

    @Test
    void processScheduledTasks_TaskPresent() {
        when(service.isAnyScheduled(EXPECTED_TIME)).thenReturn(List.of(SCHEDULE_USER_1_1));
        doNothing().when(orchestrator).operate(USER_1);

        scheduler.processScheduledTasks();

        verify(service).isAnyScheduled(EXPECTED_TIME);
        verifyNoMoreInteractions(service);

        verify(orchestrator).operate(USER_1);
        verifyNoMoreInteractions(orchestrator);
    }

    @Test
    void processScheduledTasks_noTasksPresent() {
        when(service.isAnyScheduled(EXPECTED_TIME)).thenReturn(emptyList());

        scheduler.processScheduledTasks();

        verify(service).isAnyScheduled(EXPECTED_TIME);
        verifyNoMoreInteractions(service);

        verifyNoInteractions(orchestrator);
    }

    @TestConfiguration
    @Import(MockedClockConfiguration.class)
    static class MessageSchedulerTestConfig {
        @Autowired
        Clock clock;

        @Bean
        ScheduleService scheduleService() {
            return mock(ScheduleService.class);
        }

        @Bean
        SchedulableHandlerOrchestrator orchestrator() {
            return mock(SchedulableHandlerOrchestrator.class);
        }

        @Bean
        MessageScheduler scheduler() {
            return new MessageScheduler(scheduleService(), clock, orchestrator());
        }
    }
}
