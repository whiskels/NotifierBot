package com.whiskels.telegrambot.repository;

import com.whiskels.telegrambot.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaScheduleRepository extends JpaRepository<Schedule, Integer> {
    @Query("SELECT s FROM Schedule s WHERE s.user.id=:userId ORDER BY s.hour ASC")
    List<Schedule> getAll(@Param("userId") String userId);

    @Query("DELETE FROM Schedule s WHERE s.user.id=:userId")
    int clear(@Param("userId") String userId);

    @Query("SELECT s FROM Schedule s JOIN FETCH s.user WHERE s.hour=:hour AND s.minutes=:minutes ORDER BY s.hour DESC")
    List<Schedule> getAllByHourAndMinute(@Param("hour") int hour, @Param("minutes") int minutes);
}
