package service;

import bot.Bot;
import command.Command;
import command.CommandParser;
import command.ParsedCommand;
import handler.AbstractHandler;
import handler.DefaultHandler;
import handler.ScheduleHandler;
import handler.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class MessageReceiver implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(MessageReceiver.class);
    private final int WAIT_FOR_NEW_MESSAGE_DELAY = 1_000;
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
     * Analyzes new update, verifies user and chooses command handler
     */
    private void analyzeForUpdateType(Update update) {
        String chatId = update.getMessage().getChatId().toString();

        String inputText = update.getMessage().getText();
        ParsedCommand parsedCommand = commandParser.getParsedCommand(inputText);

        if (!bot.containsUser(chatId)) {
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
            case SCHEDULE_GET:
                ScheduleHandler scheduleHandler = new ScheduleHandler(bot);
                log.info("Handler for command [" + command.toString() + "] is: " + scheduleHandler);
                return scheduleHandler;
            default:
                log.info("Handler for command [" + command.toString() + "] not Set. Return DefaultHandler");
                return new DefaultHandler(bot);
        }
    }
}
