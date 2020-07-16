package bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import security.User;
import service.Customer;
import service.JSONReader;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Main bot class
 *
 * @author whiskels
 */
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = LoggerFactory.getLogger(Bot.class);   // Logging
    private final int RECONNECT_PAUSE = 10_000;                             // Reconnect delay

    private final Map<String, User> userMap;                                // Stores all users
    private final String botName;                                           // Bot name
    private final String token;                                             // Bot token
    private List<Customer> customerList;                                    // Cached customer list

    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final String ADMIN_ID = "87971601";                              // Bot admin ID


    /*
     * Actions done on each update received
     */
    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Received new Update: '{}'", update.getMessage());
        receiveQueue.add(update);
    }

    /*
     * Establishes connection on start up
     */
    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Looking for messages");
            updateCustomers();
            log.info("Initial customer information loaded");
        } catch (TelegramApiRequestException e) {
            log.error("Unable to connect. Pause " + RECONNECT_PAUSE / 1000
                    + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }

            botConnect();
        }
    }

    /*
     * Updates Customer info
     */
    public void updateCustomers() {
        try {
            JSONReader jsonReader = new JSONReader();
            jsonReader.update();
            customerList = jsonReader.getCustomerList();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<Customer> getCustomerList() {
        return customerList;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public Bot(String botName, String token) {
        this.botName = botName;
        this.token = token;
        userMap = new HashMap<>();
    }

    public void addUser(String chatId) {
        userMap.put(chatId, new User(chatId));
    }

    public List<User> getUserList() {
        return new ArrayList<>(userMap.values());
    }

    public boolean containsUser(String chatId) {
        return userMap.containsKey(chatId);
    }

    public void updateUsers() {
        userMap.entrySet().removeIf(e -> !e.getValue().isValid());
    }

    public User getUser(String chatId) {
        return userMap.getOrDefault(chatId, null);
    }
}
