package com.whiskels.notifier.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.util.List;
import java.util.function.Supplier;

import static com.google.common.base.Suppliers.memoizeWithExpiration;
import static java.time.LocalDate.now;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.springframework.aop.framework.AopProxyUtils.ultimateTargetClass;
import static org.springframework.core.GenericTypeResolver.resolveTypeArgument;

@Slf4j
@RequiredArgsConstructor
public class MemoizingReportSupplier<T> implements ReportSupplier<T> {
    private final Loader<T> loader;
    private final Clock clock;
    private final Supplier<ReportData<T>> cache;
    private final Class<T> genericClass;

    @SuppressWarnings("unchecked")
    public MemoizingReportSupplier(Loader<T> loader, Clock clock) {
        this.loader = loader;
        this.clock = clock;
        this.genericClass = (Class<T>) resolveTypeArgument(ultimateTargetClass(loader), Loader.class);
        this.cache = memoizeWithExpiration(this::reload, 5, MINUTES);
    }

    private ReportData<T> reload() {
        log.info("Updating {} data", genericClass.getSimpleName());
        List<T> data = loader.load();
        log.debug("Found {} {} data", data.size(), genericClass.getSimpleName());
        return new ReportData<>(data, now(clock));
    }

    @Override
    public final ReportData<T> get() {
        return cache.get();
    }
}
