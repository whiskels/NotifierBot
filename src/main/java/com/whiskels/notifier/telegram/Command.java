package com.whiskels.notifier.telegram;

import lombok.Getter;

@Getter
public enum Command {
    ADMIN_TIME("", "⌚ Show bot server time"),
    ADMIN_SLACK("", "\uD83D\uDD14 Call Slack manually"),
    ADMIN_SLACK_DEBUG("", "\uD83E\uDDEA Debug Slack"),
    ADMIN_RELOAD_DATA("", "\uD83D\uDD03 Data reloading"),
    ADMIN_AUDIT("", "\uD83D\uDEC3 Audit"),
    ADMIN_MESSAGE,
    ADMIN_PROMOTE,
    ADMIN_NAME,
    ADMIN_HELP("\uD83D\uDC6E Admin panel"),

    GET_EVENT("\uD83D\uDD74️Upcoming employee events"),
    GET_DEBT("\uD83E\uDDFE Overdue debts"),
    GET_PAYMENT("\uD83D\uDCB0 Recent payments"),
    SCHEDULE("\uD83D\uDCC5 Manage schedule"),
    SCHEDULE_CLEAR,
    SCHEDULE_HELP,
    HELP,
    START,
    TOKEN("\uD83C\uDD94 Show your token");

    private final String description;
    private final String adminDescription;

    Command() {
        this.description = "";
        this.adminDescription = "";
    }

    Command(String description) {
        this.description = description;
        this.adminDescription = "";
    }

    Command(String description, String adminDescription) {
        this.description = description;
        this.adminDescription = adminDescription;
    }

    @Override
    public String toString() {
        return "/" + this.name();
    }
}
