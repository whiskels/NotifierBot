package model;

public class User {
    private final String chatId;
    private String name;
    private boolean isManager;
    private boolean isAdmin;
    private boolean isHead;

    public User(String chatId, String name, boolean isManager, boolean isHead, boolean isAdmin) {
        this.chatId = chatId;
        this.name = name;
        this.isManager = isManager;
        this.isHead = isHead;
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public boolean isManager() {
        return isManager;
    }

    public boolean isHead() {
        return isHead;
    }

    public String getChatId() {
        return chatId;
    }

    public String getName() {
        return name;
    }
}
