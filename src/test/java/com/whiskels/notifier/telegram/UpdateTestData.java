package com.whiskels.notifier.telegram;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.core.io.ClassPathResource;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.whiskels.notifier.JsonUtils.read;

@UtilityClass
public class UpdateTestData {
    public static final String HELP_MESSAGE_JSON = "/json/telegram/update/message/help.json";
    public static final String TOKEN_QUERY_JSON = "/json/telegram/update/callbackquery/token.json";

    @SneakyThrows
    public static Update update(String jsonPath) {
        return read(new ClassPathResource(jsonPath).getURL().toString(),
                        Update.class).get(0);
    }
}
