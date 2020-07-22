package handler;

import bot.Bot;
import command.Command;
import command.ParsedCommand;
import model.Customer;
import model.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class SystemHandler extends AbstractHandler {

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
                .append("[/get](/get) - get your customer overdue debts info (updated daily)")
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
        text.append(String.format("Hello. I'm  *%s*", bot.getBotUsername()))
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
        text.append(String.format("Your token is *%s*", chatId));
        sendMessage.setText(text.toString());

        return sendMessage;
    }

    /*
     * Sends overdue status message
     */
    public SendMessage getMessageGet(String chatId) {
        SendMessage sendMessage = createMessageTemplate(chatId);

        StringBuilder text = new StringBuilder();
        text.append(String.format("Overdue debts on %s", dtf.format(LocalDateTime.now().plusHours(3))))
                .append(END_LINE).append(END_LINE);
        try {
            StringBuilder list = new StringBuilder();

            final User user = bot.getUser(chatId);
            list.append(bot.getCustomerList().stream()
                    .filter(customer -> isValid(user, customer))
                    .map(Customer::toString)
                    .collect(Collectors.joining(String.format(
                            "%s%s%s", END_LINE, EMPTY_LINE, END_LINE))));

            if (list.length() == 0) {
                list.append("No overdue debts");
            }
            text.append(list.toString());

        } catch (Exception e) {
            log.error("Exception while creating message GET: {}", e.getMessage());
        }
        sendMessage.setText(text.toString());

        return sendMessage;
    }

    private boolean isValid(User user, Customer customer) {
        return user.isHead() || user.isManager() && user.getName().equals(customer.getAccountManager());
    }
}