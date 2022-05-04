package com.whiskels.notifier.telegram.util;

import com.whiskels.notifier.telegram.Command;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.EnumSet;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ParsingUtil {

    /**
     * Parses input text into hours and minutes
     * <p>
     * 1 -> 1:00
     * 12 -> 12:00
     * 123 -> 1:23
     * 1234 -> 12:34
     * 12:34 -> 12:34
     * <p>
     * 25 -> IllegalArgumentException
     * 10:62 -> IllegalArgumentException
     *
     * @param text
     * @return List of hours and minutes
     */
    //TODO refactor using strategy
    public static List<Integer> getTime(String text) {
        int hours = 0;
        int minutes = 0;
        int len = text.length();

        if (text.contains(":")) {
            int delimiter = text.indexOf(":");
            hours = Integer.parseInt(text.substring(0, delimiter));
            minutes = Integer.parseInt(text.substring(++delimiter));
        } else {
            if (len > 4) {
                throw new IllegalArgumentException();
            } else if (len == 1 || len == 3) {
                hours = Integer.parseInt(text.substring(0, 1));
                if (len == 3) {
                    minutes = Integer.parseInt(text.substring(1));
                }
            } else if (len == 2) {
                hours = Integer.parseInt(text);
            } else if (len == 4) {
                hours = Integer.parseInt(text.substring(0, 2));
                minutes = Integer.parseInt(text.substring(2));
            }
        }

        if (hours > 24 || hours < 0 || minutes < 0 || minutes > 60) {
            throw new IllegalArgumentException();
        }

        return List.of(hours, minutes);
    }

    public static Command extractCommand(String message) {
        String textCommand = message.split(" ")[0];
        return EnumSet.allOf(Command.class)
                .stream()
                .filter(command -> command.toString().equalsIgnoreCase(textCommand))
                .reduce((a, b) -> {
                    throw new IllegalStateException();
                }).orElse(Command.HELP);
    }

    public static String extractArguments(String message) {
        return message.substring(message.indexOf(" ") + 1);
    }
}
