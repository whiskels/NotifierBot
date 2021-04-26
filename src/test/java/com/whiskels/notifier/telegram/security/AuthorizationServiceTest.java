package com.whiskels.notifier.telegram.security;

import com.whiskels.notifier.telegram.handler.impl.AdminSlackHandler;
import com.whiskels.notifier.telegram.handler.impl.DebtHandler;
import com.whiskels.notifier.telegram.handler.impl.HelpHandler;
import com.whiskels.notifier.telegram.handler.impl.TokenHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

@SpringBootTest(classes = AuthorizationService.class)
class AuthorizationServiceTest {
    @Autowired
    private AuthorizationService service;

    @Test
    void testAuthorizationService() {
        assertTrue(service.authorize(AdminSlackHandler.class, USER_1));
        assertFalse(service.authorize(DebtHandler.class, USER_2));
        assertTrue(service.authorize(TokenHandler.class, USER_2));
        assertTrue(service.authorize(HelpHandler.class, USER_2));
    }
}
