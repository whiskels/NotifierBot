package com.whiskels.notifier.external;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

import javax.annotation.PostConstruct;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDate.now;

@Slf4j
@RequiredArgsConstructor
public abstract class InMemorySupplier<T> implements Supplier<T> {
    @SuppressWarnings("unchecked")
    protected final Class<T> genericClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), InMemorySupplier.class);

    @Setter(onMethod = @__(@Autowired))
    private Loader<T> loader;
    @Setter(onMethod = @__(@Autowired))
    private Clock clock;

    private LocalDate lastUpdate;
    private List<T> data;

    @PostConstruct
    @Override
    public void update() {
        log.info("Updating {} list", genericClass.getSimpleName());
        data = loader.load();
        log.info("Found {} {} data", data.size(), genericClass.getSimpleName());
        lastUpdate = now(clock);
    }

    @Override
    public final List<T> getData() {
        return data;
    }

    @Override
    public final LocalDate lastUpdate() {
        return lastUpdate;
    }
}
