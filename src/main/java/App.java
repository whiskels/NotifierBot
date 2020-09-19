import com.whiskels.telegrambot.bot.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApiContextInitializer.init();

        SpringApplication.run(Bot.class, args);
    }
}

