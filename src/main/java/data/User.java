package data;

import security.Users;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final String chatId;
    private String name;
    private boolean isManager;
    private boolean isHead;
    private Map<Integer, Integer> schedule;

    public User(String chatId) {
        this.chatId = chatId;
        isManager = isManager();
        isHead = isHead();
        schedule = new HashMap<>();
        if (isManager) {
            name = Users.getValidUser(chatId);
        }
    }

    public boolean isManager() {
        isManager = Users.isManager(chatId);
        return isManager;
    }

    public boolean isHead() {
        isHead = Users.isHead(chatId);
        return isHead;
    }

    public void addSchedule(int hours, int minutes) {
        schedule.put(hours, minutes);
    }

    public void clearSchedule() {
        schedule.clear();
    }

    public Map<Integer, Integer> getSchedule() {
        return schedule;
    }

    public String getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }
}
