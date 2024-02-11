package com.whiskels.notifier.infrastructure.admin.telegram;

import lombok.Getter;

@Getter
public enum Command {
    RETRY_REPORT(10, "\uD83D\uDD01 Retry reports"),
    RELOAD_DATA(15, "\uD83D\uDCC8 Reload data"),
    GET_LOGS(20, "\uD83D\uDD79 Fetch logs"),

    DEFAULT;

    private final String description;
    private final Integer order;

    Command() {
        this.description = "";
        this.order = Integer.MAX_VALUE;
    }

    Command(Integer order, String description) {
        this.description = description;
        this.order = order;
    }
}
