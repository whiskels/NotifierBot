package service;

import bot.Bot;
import command.Command;
import command.ParsedCommand;
import data.User;
import handler.SystemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

public class TaskScheduler implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(TaskScheduler.class);
    private final int UPDATE_DELAY = 60_000;
    private Bot bot;

    public TaskScheduler(Bot bot) {
        this.bot = bot;
    }

    /*
     * Scheduler's main loop
     */
    @Override
    public void run() {
        log.info("[STARTED] TaskScheduler.  Bot class: " + bot);
        while (true) {
            processScheduledTasks();

            try {
                Thread.sleep(UPDATE_DELAY);
            } catch (InterruptedException e) {
                log.error("Catch interrupt. Exit", e);
                return;
            }
        }
    }

    /*
     * Used to send scheduled messages
     */
    private void processScheduledTasks() {
        final LocalDateTime ldt = LocalDateTime.now().plusHours(3);
        if (ldt.getDayOfWeek() != DayOfWeek.SUNDAY && ldt.getDayOfWeek() != DayOfWeek.SATURDAY) {
            log.debug("Checking for scheduled messages");
            for (User user : bot.getUserList()) {
                if (user.getSchedule().size() != 0 && user.getSchedule().getOrDefault(ldt.getHour(), -1) == ldt.getMinute()) {
                    log.debug("Scheduled message for {} sent at {}:{}", user.getChatId(), ldt.getHour(), ldt.getMinute());
                    ParsedCommand command = new ParsedCommand();
                    command.setCommand(Command.GET);
                    new SystemHandler(bot).operate(user.getChatId(), command, null);
                }
            }
        }

        // Update Customer info daily
        if (ldt.getHour() == 2 && ldt.getMinute() == 0) {
            bot.updateCustomers();
        }
    }
}

