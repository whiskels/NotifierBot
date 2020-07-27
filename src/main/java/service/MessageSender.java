package service;

import bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public class MessageSender implements Runnable {
    private static final int SENDER_SLEEP_TIME = 1000;
    private final Logger log = LoggerFactory.getLogger(MessageSender.class);
    private final Bot bot;

    public MessageSender(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        log.info(String.format("[STARTED] MsgSender.  bot.Bot class: %s", bot));
        try {
            while (true) {
                for (Object object = bot.sendQueue.poll(); object != null; object = bot.sendQueue.poll()) {
                    log.debug(String.format("Get new msg to send %s", object));
                    send(object);
                }
                try {
                    Thread.sleep(SENDER_SLEEP_TIME);
                } catch (InterruptedException e) {
                    log.error("Interrupted while operating msg list", e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void send(Object object) {
        try {
            MessageType messageType = messageType(object);
            if (messageType == MessageType.EXECUTE) {
                BotApiMethod<Message> message = (BotApiMethod<Message>) object;
                log.debug(String.format("Use Execute for %s", object));
                bot.execute(message);
            } else {
                log.warn(String.format("Can't detect type of object. %s", object));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private MessageType messageType(Object object) {
        if (object instanceof BotApiMethod) return MessageType.EXECUTE;
        return MessageType.NOT_DETECTED;
    }

    enum MessageType {
        EXECUTE, NOT_DETECTED,
    }
}
