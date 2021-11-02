package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.common.audit.repository.AuditRepository;
import com.whiskels.notifier.telegram.annotation.BotCommand;
import com.whiskels.notifier.telegram.handler.AbstractAdminCallHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.domain.PageRequest;

import static com.whiskels.notifier.telegram.Command.ADMIN_AUDIT;
import static com.whiskels.notifier.telegram.domain.Role.ADMIN;

@Slf4j
@BotCommand(command = ADMIN_AUDIT, requiredRoles = {ADMIN})
@ConditionalOnBean(AuditRepository.class)
public class AdminAuditHandler extends AbstractAdminCallHandler<AuditRepository> {
    public AdminAuditHandler() {
        super(auditRepository -> auditRepository.getLast(PageRequest.of(0, 10)),
                null,
                "Get audit results");
    }
}
