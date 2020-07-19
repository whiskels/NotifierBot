package bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import model.User;
import model.Customer;
import repository.ScheduleRepository;
import repository.UserRepository;
import service.JSONReader;

import java.time.LocalDateTime;
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

    private final String BOT_NAME = System.getenv("BOT_NAME");        // Bot name
    private final String TOKEN = System.getenv("BOT_TOKEN");          // Bot token
    private List<Customer> customerList;                                    // Cached customer list

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();

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
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    public Bot() {
        userRepository = new UserRepository();
        scheduleRepository = new ScheduleRepository();
        updateCustomers();
    }


    public boolean containsUser(String chatId) {
        return userRepository.containsUser(chatId);
    }

    public User getUser(String chatId) {
        return userRepository.getUser(chatId);
    }

    public List<String> isAnyScheduled(LocalDateTime ldt) {
        return scheduleRepository.checkSchedule(ldt);
    }

    public void clearSchedule(String chatId) {
        scheduleRepository.clearSchedule(chatId);
    }

    public Map<Integer, List<Integer>> getSchedule(String chatId) {
        return scheduleRepository.getSchedule(chatId);
    }

    public void addSchedule(String chatId, int hour, int minutes) {
        scheduleRepository.addSchedule(chatId, hour, minutes);
    }

    public List<String> getAdmins() {
        return userRepository.getAdmins();
    }
}
