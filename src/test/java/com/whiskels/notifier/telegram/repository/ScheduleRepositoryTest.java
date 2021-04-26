package com.whiskels.notifier.telegram.repository;

import com.whiskels.notifier.AbstractRepositoryTest;
import com.whiskels.notifier.telegram.domain.Schedule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.util.List;

import static com.whiskels.notifier.telegram.ScheduleTestData.*;
import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class ScheduleRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Test
    @Sql(scripts = {"classpath:db/clear.sql", "classpath:db/add_schedule.sql"}, config = @SqlConfig(encoding = "UTF-8"))
    void testGetByChatId() {
        List<Schedule> userOneSchedule = scheduleRepository.getAll(1);
        List<Schedule> userTwoSchedule = scheduleRepository.getAll(2);
        List<Schedule> userThreeSchedule = scheduleRepository.getAll(3);

        assertEquals(2, userOneSchedule.size());
        assertTrue(userOneSchedule.containsAll(List.of(SCHEDULE_USER_1_1, SCHEDULE_USER_1_2)));

        assertEquals(1, userTwoSchedule.size());
        assertTrue(userTwoSchedule.contains(SCHEDULE_USER_2_1));

        assertTrue(userThreeSchedule.isEmpty());
    }

    @Test
    @Sql(scripts = {"classpath:db/clear.sql", "classpath:db/add_schedule.sql"}, config = @SqlConfig(encoding = "UTF-8"))
    void testDelete() {
        assertEquals(2, scheduleRepository.delete(1));
        assertTrue(scheduleRepository.getAll(1).isEmpty());

        assertEquals(1, scheduleRepository.delete(2));
        assertTrue(scheduleRepository.getAll(2).isEmpty());

        assertEquals(0, scheduleRepository.delete(3));
    }

    @Test
    @Sql(scripts = {"classpath:db/clear.sql", "classpath:db/add_users.sql"}, config = @SqlConfig(encoding = "UTF-8"))
    void testSaveSchedule() {
        Schedule expected  = new Schedule(11, 11, USER_1);
        scheduleRepository.save(expected);

        List<Schedule> actual = scheduleRepository.getAll(1);
        assertEquals(1, actual.size());

        expected.setId(actual.get(0).getId());
        assertEquals(expected, actual.get(0));

        scheduleRepository.delete(1);
    }
}
