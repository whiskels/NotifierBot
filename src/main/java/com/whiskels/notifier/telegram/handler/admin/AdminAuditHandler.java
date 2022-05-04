package com.whiskels.notifier.telegram.handler.admin;

import com.whiskels.notifier.common.audit.repository.AuditRepository;
import com.whiskels.notifier.telegram.Command;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static com.whiskels.notifier.telegram.Command.ADMIN_AUDIT;

@Service
class AdminAuditHandler extends BeanCallingHandler<AuditRepository> {
    public AdminAuditHandler() {
        super(auditRepository -> auditRepository.getLast(PageRequest.of(0, 10)),
                null,
                "Get audit results");
    }

    @Override
    public Command getCommand() {
        return ADMIN_AUDIT;
    }
}
