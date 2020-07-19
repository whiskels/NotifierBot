package handler;

import bot.Bot;
import command.Command;
import command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public abstract class AbstractHandler {
    private final String END_LINE = "\n";

    protected Bot bot;

    AbstractHandler(Bot bot) {
        this.bot = bot;
    }

    public abstract String operate(String chatId, ParsedCommand parsedCommand, Update update);

    /*
     * Sends status message to admins
     * Used when unauthorized users try to gain access to the bot
     */
    protected void sendStatusMessageToAdmin(String chatId, Command command) {
        for (String adminId : bot.getAdmins()) {
            SendMessage sendMessage = createMessageTemplate(adminId);

            StringBuilder text = new StringBuilder();
            text.append(String.format("User *%s* %s", chatId, command))
                    .append(END_LINE)
                    .append(String.format("Permissions to use bot: *%s*",
                            bot.getUser(chatId).isManager()));
            sendMessage.setText(text.toString());

            bot.sendQueue.add(sendMessage);
        }
    }

    /*
     * Creates SendMessage template with markdown enabled for user with chatId
     */
    protected SendMessage createMessageTemplate(String chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);

        return sendMessage;
    }
}
