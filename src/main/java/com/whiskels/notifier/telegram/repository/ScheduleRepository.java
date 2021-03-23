package com.whiskels.notifier.telegram.repository;

import com.whiskels.notifier.telegram.annotations.Schedulable;
import com.whiskels.notifier.telegram.domain.Schedule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
@Profile("telegram-common")
@ConditionalOnBean(annotation = Schedulable.class)
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.user.chatId=:chatId ORDER BY s.hour ASC")
    List<Schedule> getAll(@Param("chatId") int chatId);

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.user.id=:userId")
    int delete(@Param("userId") int userId);

    @Query("SELECT s FROM Schedule s JOIN FETCH s.user WHERE s.hour=:hour AND s.minutes=:minutes ORDER BY s.hour DESC")
    List<Schedule> getAllByHourAndMinute(@Param("hour") int hour, @Param("minutes") int minutes);
}
