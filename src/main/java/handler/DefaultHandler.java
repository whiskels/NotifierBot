package handler;

import bot.Bot;
import command.ParsedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

public class DefaultHandler extends AbstractHandler {
    private static final Logger log = LoggerFactory.getLogger(DefaultHandler.class);
    private final String END_LINE = "\n";

    public DefaultHandler(Bot bot) {
        super(bot);
    }

    @Override
    public String operate(String chatId, ParsedCommand parsedCommand, Update update) {
        return null;
    }
}
