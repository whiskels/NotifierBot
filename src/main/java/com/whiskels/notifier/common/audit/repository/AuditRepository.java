package com.whiskels.notifier.common.audit.repository;

import com.whiskels.notifier.common.audit.domain.AbstractAuditedEntity;
import com.whiskels.notifier.telegram.TelegramLabeled;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@NoRepositoryBean
public interface AuditRepository<T extends AbstractAuditedEntity> extends Repository<T, Integer>, TelegramLabeled {
    T save(T audit);

    <S extends T> List<S> saveAll(Iterable<S> var1);

    @Transactional(readOnly = true)
    @Query("select max(a.loadDate) from #{#entityName} a")
    LocalDate getLastUpdateDate();

    @Modifying
    @Query("delete from #{#entityName} a where a.loadDate < :date")
    int deleteByDateBefore(@Param("date") LocalDate date);

    @Transactional(readOnly = true)
    @Query("from #{#entityName} a order by a.loadDateTime desc")
    List<T> getLast(PageRequest pageRequest);

    @Override
    default String getLabel() {
        List<T> objects = getLast(PageRequest.of(0, 1));

        if (!objects.isEmpty()) {
            return objects.get(0).getClass().getSimpleName();
        }
        return "Audit";
    }
}
