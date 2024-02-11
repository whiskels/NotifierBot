package com.whiskels.notifier.infrastructure.admin.telegram.handler.log.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaperTrailEventLog {
    private List<Event> events;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Event {
        private String message;
    }
}
