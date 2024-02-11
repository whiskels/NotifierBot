package com.whiskels.notifier.infrastructure.repository;

import com.whiskels.notifier.reporting.domain.AbstractTimeStampedEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@NoRepositoryBean
public interface AbstractRepository<T extends AbstractTimeStampedEntity> extends Repository<T, Integer> {
    void save(T audit);

    <S extends T> List<S> saveAll(Iterable<S> var1);

    @Transactional
    @Modifying
    @Query("delete from #{#entityName} a where a.loadDateTime < :date")
    int deleteByDateBefore(@Param("date") LocalDateTime date);
}
