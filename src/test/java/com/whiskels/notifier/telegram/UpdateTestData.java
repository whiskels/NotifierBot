package com.whiskels.notifier.telegram;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.whiskels.notifier.ResourceUtil.readJson;

@UtilityClass
public class UpdateTestData {
    public static final String HELP_MESSAGE_JSON = "/json/telegram/update/message/help.json";
    public static final String TOKEN_QUERY_JSON = "/json/telegram/update/callbackquery/token.json";

    public static Update update(String jsonPath) {
        return readJson(jsonPath, Update.class);
    }
}
