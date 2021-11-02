package com.whiskels.notifier.external;

import com.whiskels.notifier.external.json.JsonReader;
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
public class InMemoryDataLoader<T> implements DataLoaderAndProvider<T> {
    @SuppressWarnings("unchecked")
    private final Class<T> genericClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), InMemoryDataLoader.class);
    private final String jsonUrl;
    private final String jsonNode;

    @Setter(onMethod = @__(@Autowired))
    private JsonReader jsonReader;
    @Setter(onMethod = @__(@Autowired))
    private Clock clock;

    private List<T> cachedData;
    private LocalDate lastUpdateDate;

    public InMemoryDataLoader(String jsonUrl) {
        this.jsonUrl = jsonUrl;
        this.jsonNode = null;
    }

    @Override
    public List<T> getData() {
        return cachedData;
    }

    @Override
    public LocalDate lastUpdate() {
        return lastUpdateDate;
    }

    @PostConstruct
    public List<T> update() {
        log.info("Updating {} list", genericClass.getSimpleName());
        cachedData = loadData();
        log.info("Found {} {} data after filters", cachedData.size(), genericClass.getSimpleName());
        lastUpdateDate = now(clock);
        return cachedData;
    }

    protected List<T> loadData() {
        return loadFromJson();
    }

    protected final List<T> loadFromJson() {
        if (jsonNode != null) {
            return jsonReader.read(jsonUrl, jsonNode, genericClass);
        } else {
            return jsonReader.read(jsonUrl, genericClass);
        }
    }
}
