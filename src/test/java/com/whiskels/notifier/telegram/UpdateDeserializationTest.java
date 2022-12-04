package com.whiskels.notifier.telegram;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.whiskels.notifier.JsonUtils.read;
import static com.whiskels.notifier.TestUtil.file;
import static com.whiskels.notifier.telegram.Command.HELP;
import static com.whiskels.notifier.telegram.UpdateTestData.HELP_MESSAGE_JSON;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateDeserializationTest {
    @Test
    void testUpdateDeserialization() {
        Update actual = read(file(HELP_MESSAGE_JSON), Update.class).get(0);

        assertNotNull(actual);
        assertTrue(actual.hasMessage());
        Message message = actual.getMessage();
        assertEquals(1L, message.getFrom().getId());
        assertEquals(HELP.toString(), message.getText());
    }
}
