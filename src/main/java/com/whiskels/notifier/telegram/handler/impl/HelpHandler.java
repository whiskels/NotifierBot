package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractHelpHandler;

import static com.whiskels.notifier.telegram.Command.HELP;
import static com.whiskels.notifier.telegram.Command.START;

@BotCommand(command = {HELP, START})
public class HelpHandler extends AbstractHelpHandler {
    public HelpHandler() {
        super(Command::getDescription);
    }
}
