package handler;

import bot.Bot;
import command.Command;
import command.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import data.Customer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SystemHandler extends AbstractHandler {
    private static final Logger log = LoggerFactory.getLogger(SystemHandler.class);
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private final String END_LINE = "\n";

    public SystemHandler(Bot bot) {
        super(bot);
    }

    /*
     * Chooses message template based on parsed command
     */
    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        Command command = parsedCommand.getCommand();

        switch (command) {
            case UNAUTHORIZED:
                bot.sendQueue.add(getMessageUnauthorized(chatId));
                break;
            case START:
                bot.sendQueue.add(getMessageStart(chatId));
                break;
            case HELP:
                bot.sendQueue.add(getMessageHelp(chatId));
                break;
            case TOKEN:
                bot.sendQueue.add(getMessageToken(chatId));
                break;
            case GET:
                bot.sendQueue.add(getMessageGet(chatId));
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
        text.append("*This is help message*")
                .append(END_LINE).append(END_LINE)
                .append("[/start](/start) - show start message")
                .append(END_LINE)
                .append("[/get](/get) - get your customer overdue debts info")
                .append(END_LINE)
                .append("[/token](/token) - get your token and user info")
                .append(END_LINE)
                .append("[/schedule help](/schedule help) - show schedule help message")
                .append(END_LINE)
                .append("[/help](/help) - show help message")
                .append(END_LINE);

        sendMessage.setText(text.toString());
        return sendMessage;
    }

    /*
     * Sends start message
     */
    private SendMessage getMessageStart(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);
        StringBuilder text = new StringBuilder();
        text.append("Hello. I'm  *")
                .append(bot.getBotUsername())
                .append("*")
                .append(END_LINE)
                .append("I can show overdue debts")
                .append(END_LINE)
                .append("with [/get] command");
        sendMessage.setText(text.toString());
        sendStatusMessageToAdmin(chatId, Command.START);
        return sendMessage;
    }

    /*
     * Sends user's token
     */
    private SendMessage getMessageToken(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);
        StringBuilder text = new StringBuilder();
        text.append("Your token is *")
                .append(chatId)
                .append("*")
                .append(END_LINE);
        sendMessage.setText(text.toString());
        return sendMessage;
    }

    /*
     * Sends overdue status message
     */
    public SendMessage getMessageGet(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);
        StringBuilder text = new StringBuilder();
        text.append("Overdue debts on ")
                .append(dtf.format(LocalDateTime.now()))
                .append(END_LINE).append(END_LINE);
        try {
            for (Customer customer : bot.getCustomerList()) {
                if (bot.getUser(chatId).isAdmin()
                        || bot.getUser(chatId).isValid()
                        && customer.getAccountManager().equals(bot.getUser(chatId).getName())) {
                    text.append(customer.toString())
                            .append(END_LINE)
                            .append("---------------------------")
                            .append(END_LINE);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        sendMessage.setText(text.toString());
        return sendMessage;
    }


    /*
     * Sends message to unauthorized users
     */
    private SendMessage getMessageUnauthorized(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);
        StringBuilder text = new StringBuilder();
        text.append("Your token is *")
                .append(chatId)
                .append("*")
                .append(END_LINE);
        text.append("Please contact your supervisor to gain access");
        sendMessage.setText(text.toString());
        sendStatusMessageToAdmin(chatId, Command.UNAUTHORIZED);
        return sendMessage;
    }
}