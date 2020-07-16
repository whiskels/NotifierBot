import bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import security.Token;
import service.MessageReceiver;
import service.MessageSender;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        ApiContextInitializer.init();
        logger.info("Api Context initialized");

        Bot bot = new Bot(Token.getBotName(), Token.getBotToken());
        logger.info("Bot created");

        MessageReceiver messageReceiver = new MessageReceiver(bot);
        logger.info("MessageReceiver created");
        MessageSender messageSender = new MessageSender(bot);
        logger.info("MessageSender created");


        bot.botConnect();

        Thread receiver = new Thread(messageReceiver);
        receiver.setDaemon(true);
        receiver.setName("MsgReceiver");
        receiver.setPriority(3);
        receiver.start();

        Thread sender = new Thread(messageSender);
        sender.setDaemon(true);
        sender.setName("MsgSender");
        sender.setPriority(1);
        sender.start();

        sendStartReport(bot);
    }

    /*
     * Send bot admin message about successful initialization
     */
    private static void sendStartReport(Bot bot) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(bot.ADMIN_ID);
        sendMessage.setText("Bot start up is successful");
        bot.sendQueue.add(sendMessage);
        logger.info("Start report sent to Admin");
    }
}

