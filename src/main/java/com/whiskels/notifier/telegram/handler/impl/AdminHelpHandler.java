package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.Command;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractHelpHandler;

import static com.whiskels.notifier.telegram.Command.ADMIN_HELP;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

@BotCommand(command = ADMIN_HELP, requiredRoles = ADMIN)
public class AdminHelpHandler extends AbstractHelpHandler {
    public AdminHelpHandler() {
        super(Command::getAdminDescription);
    }
}
