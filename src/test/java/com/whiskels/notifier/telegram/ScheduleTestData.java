package com.whiskels.notifier.telegram;

import com.whiskels.notifier.telegram.domain.Schedule;
import lombok.experimental.UtilityClass;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;

@UtilityClass
public class ScheduleTestData {
    public final Schedule SCHEDULE_USER_1_1 = new Schedule(4, 10, 0, USER_1);
    public final Schedule SCHEDULE_USER_1_2 = new Schedule(5, 15, 0, USER_1);
    public final Schedule SCHEDULE_USER_2_1 = new Schedule(6, 23, 59, USER_2);
}
