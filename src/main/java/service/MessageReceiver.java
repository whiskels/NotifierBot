package service;

import bot.Bot;
import command.Command;
import command.ParsedCommand;
import command.CommandParser;
import handler.AbstractHandler;
import handler.DefaultHandler;
import handler.ScheduleHandler;
import handler.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;
import security.User;
import security.Users;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class MessageReceiver implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MessageReceiver.class);
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1_000;
    private final int WAIT_ONE_MINUTE = 60_000;
    private int counter = 59;
    private final int COUNTER_DEFAULT_VALUE = 59;
    private Bot bot;
    private CommandParser commandParser;

    public MessageReceiver(Bot bot) {
        this.bot = bot;
        commandParser = new CommandParser(bot.getBotUsername());
    }

    /*
     * Receivers main loop
     */
    @Override
    public void run() {
        log.info("[STARTED] MsgReceiver.  Bot class: " + bot);
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                log.debug("New object for analyze in queue " + object.toString());
                analyze(object);
            }

            if (counter <= 0) {
                sendScheduledMessages();
                counter = COUNTER_DEFAULT_VALUE;
            } else {
                counter--;
            }

            try {
                Thread.sleep(WAIT_FOR_NEW_MESSAGE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }
    }

    /*
     * Analyzes if new object is Update
     */
    private void analyze(Object object) {
        if (object instanceof Update) {
            Update update = (Update) object;
            log.debug("Update received: " + update.toString());
            analyzeForUpdateType(update);
        } else log.warn("Cant operate type of object: " + object.toString());
    }

    /*
     * Used to send scheduled messages
     */
    private boolean sendScheduledMessages() {
        boolean isAnyMessageSent = false;

        final LocalDateTime ldt = LocalDateTime.now().plusHours(3);
        if (ldt.getDayOfWeek() != DayOfWeek.SUNDAY && ldt.getDayOfWeek() != DayOfWeek.SATURDAY) {
            log.debug("Checking for scheduled messages");
            for (User user : bot.getUserList()) {
                if (user.getSchedule().size() != 0) {
                    if (user.getSchedule().getOrDefault(ldt.getHour(), -1) == ldt.getMinute()) {
                        log.debug("Scheduled message for {} sent at {}:{}", user.getChatId(), ldt.getHour(), ldt.getMinute());
                        ParsedCommand command = new ParsedCommand();
                        command.setCommand(Command.GET);
                        new SystemHandler(bot).operate(user.getChatId(), command, null);
                        isAnyMessageSent = true;
                    }
                }
            }
        }

        if (ldt.getHour() == 2 && ldt.getMinute() == 0) {
            bot.updateCustomers();
        }
        return isAnyMessageSent;
    }

    /*
     * Analyzes new update, verifies user and chooses command handler
     */
    private void analyzeForUpdateType(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        String inputText = update.getMessage().getText();
        ParsedCommand parsedCommand = commandParser.getParsedCommand(inputText);

        if (Users.isValidUser(chatId)) {
            if (!bot.containsUser(chatId)) {
                bot.addUser(chatId);
            }
        } else {
            if (bot.containsUser(chatId)) {
                bot.updateUsers();
            }
            parsedCommand.setCommand(Command.UNAUTHORIZED);
        }

        AbstractHandler handlerForCommand = getHandlerForCommand(parsedCommand.getCommand());
        handlerForCommand.operate(chatId, parsedCommand, update);
    }

    /*
     * Chooses handler for input command
     */
    private AbstractHandler getHandlerForCommand(Command command) {
        if (command == null) {
            log.warn("Null command");
            return new DefaultHandler(bot);
        }

        switch (command) {
            case START:
            case HELP:
            case TOKEN:
            case GET:
            case UNAUTHORIZED:
                SystemHandler systemHandler = new SystemHandler(bot);
                log.info("Handler for command [" + command.toString() + "] is: " + systemHandler);
                return systemHandler;
            case SCHEDULE_ADD:
            case SCHEDULE_HELP:
            case SCHEDULE_CLEAR:
            case SCHEDULE_INFO:
                ScheduleHandler scheduleHandler = new ScheduleHandler(bot);
                log.info("Handler for command [" + command.toString() + "] is: " + scheduleHandler);
                return scheduleHandler;
            default:
                log.info("Handler for command [" + command.toString() + "] not Set. Return DefaultHandler");
                return new DefaultHandler(bot);
        }
    }
}
