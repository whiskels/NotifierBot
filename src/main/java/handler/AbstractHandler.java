package handler;

import bot.Bot;
import command.Command;
import command.ParsedCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import security.Users;

public abstract class AbstractHandler {
    private final String END_LINE = "\n";

    Bot bot;

    AbstractHandler(Bot bot) {
                this.bot = bot;
    }

    public abstract String operate(String chatId, ParsedCommand parsedCommand, Update update);

    /*
     * Sends status message to admin
     * Used when unauthorized users try to gain access to the bot
     */
    protected void sendStatusMessageToAdmin(String chatId, Command command) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(bot.ADMIN_ID);
        sendMessage.enableMarkdown(true);
        StringBuilder text = new StringBuilder();
        text.append("User *")
                .append(chatId)
                .append("* ")
                .append(command)
                .append(END_LINE)
                .append("Permissions to use bot: *")
                .append(Users.isValidUser(chatId))
                .append("*");
        sendMessage.setText(text.toString());
        bot.sendQueue.add(sendMessage);
    }
}
