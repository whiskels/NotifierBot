package com.whiskels.telegrambot.bot.command;

import com.whiskels.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.whiskels.telegrambot.bot.command.Command.*;
import static com.whiskels.telegrambot.util.TelegramUtils.*;

@Component
@PropertySource("classpath:bot/bot.properties")
@Slf4j
public class HelpHandler extends AbstractBaseHandler {
    @Value("${bot.name.test}")
    private String botUsername;

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> operate(User user, String message) {
        return Collections.singletonList(createMessageTemplate(user)
                .setText(String.format("Hello. I'm *%s*%nHere are your available commands%nUse [/help] command to display this message", botUsername))
                .setReplyMarkup(createIndividualInlineKeyBoard(user)));
    }

    private InlineKeyboardMarkup createIndividualInlineKeyBoard(User user) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> inlineKeyboardButtonsRows = new ArrayList<>();
        inlineKeyboardButtonsRows.add(UNAUTHORIZED_ROW);
        if (user.isManager()) {
            inlineKeyboardButtonsRows.add(GET_ROW);
            inlineKeyboardButtonsRows.add(SCHEDULE_ADD_ROW);
            inlineKeyboardButtonsRows.add(SCHEDULE_MANAGE_ROW);
        }

        return inlineKeyboardMarkup.setKeyboard(inlineKeyboardButtonsRows);
    }


    @Override
    public Command supportedCommand() {
        return HELP;
    }
}
