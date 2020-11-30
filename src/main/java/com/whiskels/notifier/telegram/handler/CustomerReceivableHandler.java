package com.whiskels.notifier.telegram.handler;

import com.whiskels.notifier.model.User;
import com.whiskels.notifier.service.CustomerReceivableService;
import com.whiskels.notifier.telegram.annotations.BotCommand;
import com.whiskels.notifier.telegram.annotations.RequiredRoles;
import com.whiskels.notifier.telegram.builder.MessageBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.whiskels.notifier.model.Role.ADMIN;

/**
 * Sends customer receivable report
 * <p>
 * Available to: Admin
 */
@Component
@Slf4j
@BotCommand(command = "/GET_RECEIVABLE", message = "Get customer receivables")
@Profile({"telegram", "telegram-test"})
public class CustomerReceivableHandler extends AbstractBaseHandler {
    private final CustomerReceivableService customerReceivableService;

    public CustomerReceivableHandler(CustomerReceivableService customerReceivableService) {
        this.customerReceivableService = customerReceivableService;
    }

    @Override
    @RequiredRoles(roles = {ADMIN})
    public List<BotApiMethod<Message>> handle(User user, String message) {
        log.debug("Preparing /GET_RECEIVABLE");
        MessageBuilder builder = MessageBuilder.create(user)
                .line(customerReceivableService.dailyReport());

        return List.of(builder.build());
    }
}

