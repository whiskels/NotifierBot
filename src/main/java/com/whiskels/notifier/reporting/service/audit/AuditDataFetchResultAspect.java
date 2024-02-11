package com.whiskels.notifier.reporting.service.audit;

import com.whiskels.notifier.reporting.service.ReportData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Aspect
@Configuration
@EnableAspectJAutoProxy
@RequiredArgsConstructor
class AuditDataFetchResultAspect {
    private final LoadAuditRepository auditRepository;

    @Around(value = "@annotation(dataFetchResultAudit)")
    public Object aroundAdvice(
            final ProceedingJoinPoint joinPoint,
            final AuditDataFetchResult dataFetchResultAudit
    ) throws Throwable {
        var reportType = dataFetchResultAudit.reportType();
        log.info("[{}] Fetching...", reportType);

        Object result = joinPoint.proceed();

        int count = 0;
        if (result instanceof ReportData) {
            count = ((ReportData<?>) result).data().size();
        } else if (result != null) {
            count = 1;
        }
        auditRepository.save(LoadAudit.loadAudit(count, reportType));
        log.info("[{}] DataFetchResult Audit results saved: {} from method: {}", reportType, count, joinPoint.getSignature().getName());
        return result;
    }
}
