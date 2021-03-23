package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.domain.User;
import com.whiskels.notifier.external.receivable.service.ReceivableService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import com.whiskels.notifier.telegram.handler.AbstractBaseHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

/**
 * Sends customer receivable report
 * <p>
 * Available to: Admin
 */
@Slf4j
@BotCommand(command = "/GET_RECEIVABLE", message = "Get customer receivables", requiredRoles = {ADMIN})
@ConditionalOnBean(ReceivableService.class)
public class CustomerReceivableHandler extends AbstractBaseHandler {
    private final ReceivableService receivableService;

    public CustomerReceivableHandler(ReceivableService receivableService) {
        this.receivableService = receivableService;
    }

    @Override
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /GET_RECEIVABLE");
        MessageBuilder builder = MessageBuilder.create(user)
                .line(receivableService.dailyReport());

        return List.of(builder.build());
    }
}

