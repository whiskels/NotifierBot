package com.whiskels.notifier.telegram.util;

import com.whiskels.notifier.telegram.Command;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.lang.Integer.parseInt;

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
    public static ParsedTime getTime(String text) {
        ParsedTime result = ParsedTime.parse(text);
        result.validate();
        return result;
    }

    @Value
    public static class ParsedTime {
        private static EnumSet<TimeStrategy> STRATEGIES = EnumSet.allOf(TimeStrategy.class);

        int hours;
        int minutes;

        public static ParsedTime parse(String text) {
            return STRATEGIES.stream()
                    .filter(strategy -> strategy.checker.test(text))
                    .findFirst()
                    .map(strategy -> strategy.converter.apply(text))
                    .orElseThrow(IllegalArgumentException::new);
        }

        public void validate() {
            if (hours >= 24 || hours < 0 || minutes < 0 || minutes >= 60) {
                throw new IllegalArgumentException();
            }
        }
    }

    @RequiredArgsConstructor
    private enum TimeStrategy {
        COLON_SEPARATED(text -> text.contains(":"), text -> {
            int delimiter = text.indexOf(":");
            int hours = parseInt(text.substring(0, delimiter));
            int minutes = parseInt(text.substring(++delimiter));
            return new ParsedTime(hours, minutes);
        }),
        H(text -> text.length() == 1, text -> {
            return new ParsedTime(parseInt(text), 0);
        }),
        HH(text -> text.length() == 2, text -> {
            return new ParsedTime(parseInt(text), 0);
        }),
        HMM(text -> text.length() == 3, text -> {
            return new ParsedTime(parseInt(text.substring(0, 1)), parseInt(text.substring(1)));
        }),
        HHMM(text -> text.length() == 4, text -> {
            return new ParsedTime(parseInt(text.substring(0, 2)), parseInt(text.substring(2)));
        });

        private final Predicate<String> checker;
        private final Function<String, ParsedTime> converter;
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
