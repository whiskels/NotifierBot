package com.whiskels.notifier.external.json;

import com.whiskels.notifier.external.Loader;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public abstract class JsonLoader<T> implements Loader<T> {
    @SuppressWarnings("unchecked")
    protected final Class<T> genericClass = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), JsonLoader.class);
    protected final String jsonUrl;
    protected final String jsonNode;

    @Setter(onMethod = @__(@Autowired))
    private JsonReader jsonReader;

    public JsonLoader(String jsonUrl) {
        this.jsonUrl = jsonUrl;
        this.jsonNode = null;
    }

    @Override
    public List<T> load() {
        return loadFromJson();
    }

    protected final List<T> loadFromJson() {
        return loadFromJson(jsonUrl, jsonNode);
    }

    protected final List<T> loadFromJson(String jsonUrl) {
        return loadFromJson(jsonUrl, null);
    }

    protected final List<T> loadFromJson(String jsonUrl, String jsonNode) {
        if (jsonNode != null) {
            return jsonReader.read(jsonUrl, jsonNode, genericClass);
        } else {
            return jsonReader.read(jsonUrl, genericClass);
        }
    }
}
