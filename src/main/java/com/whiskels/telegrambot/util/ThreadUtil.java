package com.whiskels.telegrambot.util;

public class ThreadUtil {
    public static Thread getThread(Runnable runnable, String name, int priority) {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.setName(name);
        thread.setPriority(priority);

        return thread;
    }
}
