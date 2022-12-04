package com.whiskels.notifier.telegram.security;

import com.whiskels.notifier.telegram.event.SendMessageCreationEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SecuredTestConfig {
    @Value("${telegram.bot.admin:}")
    private String adminChatId;
    @Autowired
    private SendMessageCreationEventPublisher publisher;

    @Bean
    public SecuredAspect securedAspect() {
        return new SecuredAspect(adminChatId, publisher);
    }
}
