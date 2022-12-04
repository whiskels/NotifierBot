package com.whiskels.notifier.external.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Collection;

import static com.whiskels.notifier.external.audit.LoadAudit.loadAudit;

@Slf4j
@Aspect
@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
class AuditedAspect {
    private final LoadAuditRepository auditRepository;

    @AfterReturning(value = "@annotation(audit) && execution(* *(..))", returning = "result")
    private void saveAudit(Object result, Audit audit) {
        int count = 1;
        if (Collection.class.isAssignableFrom(result.getClass())) {
            count = ((Collection<?>) result).size();
        }
        var loader = audit.loader();
        auditRepository.save(loadAudit(count, loader));
        log.info("[{}] LoadAudit results saved: {}", loader, count);
    }
}
