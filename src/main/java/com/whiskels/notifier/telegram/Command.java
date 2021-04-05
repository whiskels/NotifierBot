package com.whiskels.notifier.telegram;

public enum Command {
    ADMIN_MESSAGE,
    ADMIN_PROMOTE,
    ADMIN_TIME("Show bot server time"),
    ADMIN_NAME,
    GET_BIRTHDAY("Upcoming birthdays"),
    GET_DEBT("Overdue debts report"),
    GET_PAYMENT("Payment report"),
    SCHEDULE("Manage schedule"),
    SCHEDULE_CLEAR,
    SCHEDULE_HELP,
    HELP,
    START,
    TOKEN("Show your token");

    private final String description;

    Command() {
        this.description = "";
    }

    Command(String description) {
        this.description = description;
    }


    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "/" + this.name();
    }
}
