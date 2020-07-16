package command;

public class ParsedCommand {
    private Command command = Command.NONE;
    private String text = "";

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
