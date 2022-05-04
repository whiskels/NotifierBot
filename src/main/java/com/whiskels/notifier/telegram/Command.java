package com.whiskels.notifier.telegram;

import lombok.Getter;

@Getter
public enum Command {
    ADMIN_TIME(1, "⌚ Show bot server time"),
    ADMIN_AUDIT(2, "\uD83D\uDEC3 Audit"),
    ADMIN_SLACK(3, "\uD83D\uDD14 Call Slack manually"),
    ADMIN_RELOAD_DATA(4, "\uD83D\uDD03 Data reloading"),

    GET_EVENT(10, "\uD83D\uDD74️Upcoming employee events"),
    GET_DEBT(11, "\uD83E\uDDFE Overdue debts"),
    GET_PAYMENT(12, "\uD83D\uDCB0 Recent payments"),
    SCHEDULE(100, "\uD83D\uDCC5 Manage schedule"),
    SCHEDULE_CLEAR,
    SCHEDULE_HELP,
    HELP,
    START,
    TOKEN(500, "\uD83C\uDD94 Show your token");

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

    @Override
    public String toString() {
        return "/" + this.name();
    }
}
