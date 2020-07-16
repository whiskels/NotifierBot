package data;

import security.Users;

import java.util.HashMap;
import java.util.Map;

public class User {
    private final String chatId;
    private String name;
    private boolean isValid;
    private boolean isAdmin;
    private Map<Integer, Integer> schedule;

    public User(String chatId) {
        this.chatId = chatId;
        isValid = Users.isValidUser(chatId);
        isAdmin = Users.isAdmin(chatId);
        schedule = new HashMap<>();
        if (isValid) {
            name = Users.getValidUser(chatId);
        }
    }

    public boolean isValid() {
        isValid = Users.isValidUser(chatId);
        return isValid;
    }

    public boolean isAdmin() {
        isAdmin = Users.isAdmin(chatId);
        return isAdmin;
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
