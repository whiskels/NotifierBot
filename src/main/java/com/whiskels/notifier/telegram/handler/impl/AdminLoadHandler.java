package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.external.DataLoader;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractAdminCallHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

import static com.whiskels.notifier.telegram.Command.ADMIN_RELOAD_DATA;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

@Slf4j
@BotCommand(command = ADMIN_RELOAD_DATA, requiredRoles = {ADMIN})
@ConditionalOnBean(DataLoader.class)
public class AdminLoadHandler extends AbstractAdminCallHandler<DataLoader> {
    public AdminLoadHandler() {
        super(DataLoader::update, null,"Reload data");
    }
}
