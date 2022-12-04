package com.whiskels.notifier.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * <p>
 * Handling Hibernate lazy-loading
 *
 * @link https://github.com/FasterXML/jackson
 * @link https://github.com/FasterXML/jackson-datatype-hibernate
 * @link https://github.com/FasterXML/jackson-docs/wiki/JacksonHowToCustomSerializers
 */
//TODO remove
class JacksonObjectMapper extends ObjectMapper {
    private static final ObjectMapper MAPPER = new JacksonObjectMapper();

    private JacksonObjectMapper() {
//        registerModule(new Hibernate5Module());
//
//        registerModule(new JavaTimeModule());
//        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
//
//        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
//        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}