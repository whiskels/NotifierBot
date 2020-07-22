package handler;

import bot.Bot;
import command.Command;
import command.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ScheduleHandler extends AbstractHandler {
    private static final Logger log = LoggerFactory.getLogger(SystemHandler.class);

    public ScheduleHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command = parsedCommand.getCommand();

        switch (command) {
            case SCHEDULE_HELP:
                bot.sendQueue.add(getMessageHelp(chatId));
                break;
            case SCHEDULE_ADD:
                bot.sendQueue.add(getMessageScheduled(chatId, parsedCommand.getText()));
                break;
            case SCHEDULE_CLEAR:
                bot.sendQueue.add(getMessageClear(chatId));
                break;
            case SCHEDULE_GET:
                bot.sendQueue.add(getMessageInfo(chatId));
                break;
            default:
                return "No command specified";
        }
        return "";
    }

    /*
     * Sends help message
     */
    private SendMessage getMessageHelp(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);

        StringBuilder text = new StringBuilder();
        text.append("*Help message for /schedule command*")
                .append(END_LINE)
                .append(END_LINE)
                .append("[/schedule *time*](/schedule time) - set daily message at time. Examples: ")
                .append(END_LINE)
                .append("   /schedule 1 - 01:00")
                .append(END_LINE)
                .append("   /schedule 10 - 10:00")
                .append(END_LINE)
                .append("   /schedule 1230 - 12:30")
                .append(END_LINE)
                .append("Please note that daily messages are not sent on *sundays and saturdays*!")
                .append(END_LINE)
                .append("[/schedule get](/schedule get) - show current schedule state")
                .append(END_LINE)
                .append("[/schedule clear](/schedule clear) - clear schedule")
                .append(END_LINE)
                .append("[/schedule help](/schedule help) - show help message")
                .append(END_LINE);
        sendMessage.setText(text.toString());

        return sendMessage;
    }

    /*
     * Sends schedule status message
     */
    private SendMessage getMessageInfo(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);

        StringBuilder text = new StringBuilder();
        text.append("*Your current schedule:*").append(END_LINE);

        final Map<Integer, List<Integer>> schedule = bot.getSchedule(chatId);
        if (schedule.size() == 0) {
            text.append("No messages scheduled");
        } else {
            text.append(schedule.entrySet().stream()
                    .flatMap(e -> e.getValue().stream()
                            .map(s -> new AbstractMap.SimpleImmutableEntry(e.getKey(), s)))
                    .map(e -> String.format("%02d:%02d", e.getKey(), e.getValue()))
                    .collect(Collectors.joining(", ")));
        }
        sendMessage.setText(text.toString());

        return sendMessage;
    }

    /*
     * Sends message that schedule is cleared
     */
    private SendMessage getMessageClear(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);

        bot.clearSchedule(chatId);

        StringBuilder text = new StringBuilder();

        text.append("Your schedule was cleared");
        sendMessage.setText(text.toString());

        return sendMessage;
    }

    /*
     * Sets up schedule
     */
    private SendMessage getMessageScheduled(String chatId, String message) {
        SendMessage sendMessage = createMessageTemplate(chatId);

        StringBuilder text = new StringBuilder();
        try {
            int hours = 0;
            int minutes = 0;
            int len = message.length();

            if (len > 4) {
                throw new IllegalArgumentException();
            } else if (len == 1 || len == 3) {
                hours = Integer.parseInt(message.substring(0, 1));
                if (len == 3) {
                    minutes = Integer.parseInt(message.substring(1));
                }
            } else if (len == 2) {
                hours = Integer.parseInt(message);
            } else if (len == 4) {
                hours = Integer.parseInt(message.substring(0, 2));
                minutes = Integer.parseInt(message.substring(2));
            }

            if (hours > 24 || hours < 0 || minutes < 0 || minutes > 60) {
                throw new IllegalArgumentException();
            }

            bot.addSchedule(chatId, hours, minutes);
            text.append("Scheduled status messages to")
                    .append(END_LINE)
                    .append("be sent daily at ")
                    .append(String.format("*%02d:%02d*", hours, minutes))
                    .append(END_LINE)
                    .append("To reset schedule use '/schedule clear' command");
        } catch (Exception e) {
            text.append("You've entered invalid time")
                    .append(END_LINE);
            text.append("Please try again");
            log.debug("Incorrect schedule time {}", message);
        }
        sendMessage.setText(text.toString());

        return sendMessage;
    }
}
