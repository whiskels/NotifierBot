package repository;

import bot.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;

public abstract class AbstractRepository {
    private static final Logger log = LoggerFactory.getLogger(AbstractRepository.class);

    private static final String DB_URL = System.getenv("DATABASE_URL");


    protected Connection getConnection() {
        Connection connection = null;
        try {
            URI dbUri = new URI(DB_URL);

            String username = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            connection =  DriverManager.getConnection(dbUrl, username, password);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return connection;
    }
}
