package com.whiskels.notifier.telegram.handler.impl;

import com.whiskels.notifier.MockedClockConfiguration;
import com.whiskels.notifier.telegram.handler.AbstractHandlerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static com.whiskels.notifier.telegram.UserTestData.USER_1;
import static com.whiskels.notifier.telegram.UserTestData.USER_2;
import static java.lang.String.format;

@Import(MockedClockConfiguration.class)
@SpringBootTest(classes = {HelpHandler.class, TokenHandler.class, AdminTimeHandler.class},
properties = "telegram.bot.name=TESTBOT")
class HelpHandlerTest extends AbstractHandlerTest {
    private static final String EXPECTED_TEXT = format("Hello. I'm *TESTBOT*%n" +
            "Here are your available commands%n" +
            "Use [/help] command to display this message%n");
    private static final String REPLY_MARKUP_UNAUTH = "InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=Show your token, url=null, callbackData=/TOKEN, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null)]])";
    private static final String REPLY_MARKUP_ADMIN = "InlineKeyboardMarkup(keyboard=[[InlineKeyboardButton(text=Show your token, url=null, callbackData=/TOKEN, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null)], [InlineKeyboardButton(text=Show bot server time, url=null, callbackData=/ADMIN_TIME, callbackGame=null, switchInlineQuery=null, switchInlineQueryCurrentChat=null, pay=null, loginUrl=null)]])";
    @Autowired
    private HelpHandler helpHandler;

    @BeforeEach
    public void setHandler() {
        handler = helpHandler;
    }

    @Test
    void testHelpHandler_Authorized() {
        testInteraction(USER_1, EXPECTED_TEXT, REPLY_MARKUP_ADMIN);
    }

    @Test
    void testHelpHandler_Unauthorized() {
        testInteraction(USER_2, EXPECTED_TEXT, REPLY_MARKUP_UNAUTH);
    }
}
