package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static java.lang.String.format;

@SpringBootTest(classes = TokenHandler.class)
class TokenHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_TEXT = format("Your token is *1*%nYour roles are: ADMIN%n");

    @Autowired
    private TokenHandler tokenHandler;

    @Test
    void testTokenHandler() {
        handler = tokenHandler;
        testInteraction(USER_1, EXPECTED_TEXT);
    }
}
