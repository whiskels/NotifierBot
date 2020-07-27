package command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandParser {
    private static final Logger log = LoggerFactory.getLogger(CommandParser.class);

    private static final String PREFIX = "/";
    private String botName;

    public CommandParser(String botName) {
        this.botName = botName;
    }

    public ParsedCommand getParsedCommand(String text) {
        ParsedCommand result = new ParsedCommand();
        if (text.startsWith(PREFIX)) {
            switch (text.substring(1).split(" ")[0].toUpperCase()) {
                case "START":
                    result.setCommand(Command.START);
                    break;
                case "HELP":
                    result.setCommand(Command.HELP);
                    break;
                case "TOKEN":
                    result.setCommand(Command.TOKEN);
                    break;
                case "GET":
                    result.setCommand(Command.GET);
                    break;
                case "SCHEDULE":
                    String value;
                    if (text.contains(" ")) {
                         value = text.split(" ")[1].toUpperCase();
                } else {
                        value = text.toUpperCase();
                    }
                    switch (value) {
                        case "HELP":
                            result.setCommand(Command.SCHEDULE_HELP);
                            break;
                        case "CLEAR":
                            result.setCommand(Command.SCHEDULE_CLEAR);
                            break;
                        case "GET":
                            result.setCommand(Command.SCHEDULE_GET);
                            break;
                        default:
                            result.setCommand(Command.SCHEDULE_ADD);
                            result.setText(value);
                            break;
                    }
                    break;
                case "SCHEDULE_HELP":
                    result.setCommand(Command.SCHEDULE_HELP);
                    break;
                case "SCHEDULE_CLEAR":
                    result.setCommand(Command.SCHEDULE_CLEAR);
                    break;
                case "SCHEDULE_GET":
                    result.setCommand(Command.SCHEDULE_GET);
                    break;
                case "ADMIN_MESSAGE":
                    result.setCommand(Command.ADMIN_MESSAGE);
                    result.setText(text.substring(text.indexOf(" ")));
                    break;
                default:
                    result.setCommand(Command.NONE);
            }
        }
        log.debug("Command {} from text: {}", result.getCommand(), text);
        return result;
    }
}

