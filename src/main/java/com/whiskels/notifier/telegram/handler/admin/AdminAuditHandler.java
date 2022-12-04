package com.whiskels.notifier.telegram.handler.admin;

import com.whiskels.notifier.common.audit.AuditRepository;
import com.whiskels.notifier.telegram.Command;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.whiskels.notifier.telegram.Command.ADMIN_AUDIT;

@Service
class AdminAuditHandler extends BeanCallingHandler<AuditRepository<?>> {
    public AdminAuditHandler(List<AuditRepository<?>> auditRepositoryList) {
        super("Get audit results", auditRepositoryList,
                auditRepository -> auditRepository.getLast(PageRequest.of(0, 10)));
    }

    @Override
    public Command getCommand() {
        return ADMIN_AUDIT;
    }
}
