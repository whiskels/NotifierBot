package service;

import bot.Bot;
import command.Command;
import command.CommandParser;
import command.ParsedCommand;
import handler.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageReceiver implements Runnable {
    private static final String HELPER_LOG = "Handler for command [%s] is: %s";
    private static final int WAIT_FOR_NEW_MESSAGE_DELAY = 1_000;
    private final Logger log = LoggerFactory.getLogger(MessageReceiver.class);
    private final Bot bot;
    private final CommandParser commandParser;

    public MessageReceiver(Bot bot) {
        this.bot = bot;
        commandParser = new CommandParser(bot.getBotUsername());
    }

    /*
     * Receivers main loop
     */
    @Override
    public void run() {
        log.info(String.format("[STARTED] MsgReceiver.  Bot class: %s", bot));
        while (true) {
            for (Object object = bot.receiveQueue.poll(); object != null; object = bot.receiveQueue.poll()) {
                log.debug(String.format("New object to analyze in queue %s", object.toString()));
                analyze(object);
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
            log.debug(String.format("Update received: %s", update.toString()));
            analyzeForUpdateType(update);
        } else log.warn(String.format("Cant operate type of object: %s", object.toString()));
    }

    /*
     * Analyzes new update, verifies user and chooses command handler
     */
    private void analyzeForUpdateType(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        String inputText = update.getMessage().getText();
        ParsedCommand parsedCommand = commandParser.getParsedCommand(inputText);
        AbstractHandler handlerForCommand = getHandlerForCommand(parsedCommand.getCommand());

        if (!bot.containsUser(chatId)) {
            bot.addUser(chatId);
        }

        if (!bot.getUser(chatId).isManager() ||
                handlerForCommand.getClass().equals(AdminHandler.class) &&
                        !bot.getAdmins().contains(chatId)) {
            parsedCommand.setCommand(Command.UNAUTHORIZED);
        }

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
                log.info(String.format(HELPER_LOG, command.toString(), systemHandler));
                return systemHandler;
            case SCHEDULE_ADD:
            case SCHEDULE_HELP:
            case SCHEDULE_CLEAR:
            case SCHEDULE_GET:
                ScheduleHandler scheduleHandler = new ScheduleHandler(bot);
                log.info(String.format(HELPER_LOG, command.toString(), scheduleHandler));
                return scheduleHandler;
            case ADMIN_MESSAGE:
                AdminHandler adminHandler = new AdminHandler(bot);
                log.info(String.format(HELPER_LOG, command.toString(), adminHandler));
                return adminHandler;
            default:
                log.info(String.format(HELPER_LOG, command.toString(), "default handler"));
                return new DefaultHandler(bot);
        }
    }
}
