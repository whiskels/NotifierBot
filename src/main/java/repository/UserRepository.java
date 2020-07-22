package repository;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import exception.IllegalUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends AbstractRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private static final String getSQL = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String getAdminsSQL = "SELECT * FROM USERS WHERE ADMIN = TRUE";
    private static final String getUsersSQL = "SELECT * FROM USERS WHERE MANAGER = TRUE";

    public User getUser(String chatId) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(getSQL)) {
            stmt.setString(1, chatId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getString("user_id"),
                        rs.getString("name"),
                        rs.getBoolean("manager"),
                        rs.getBoolean("head"),
                        rs.getBoolean("admin"));

            } else {
                throw new IllegalUserException();
            }
        } catch (Exception e) {
            log.error("Exception while trying to get user {}: {}",
                    chatId, e.getMessage());
            return null;
        }
    }

    public boolean containsUser(String chatId) {
        return getUser(chatId) != null;
    }

    private List<String> getUsersByRole(String preparedSQL) {
        List<String> result = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(preparedSQL)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getString("user_id"));
            }
        } catch (Exception e) {
            log.error("Exception while trying to get users: {}",
                    e.getMessage());
        }
        return result;
    }

    public List<String> getUsers() {
        return getUsersByRole(getUsersSQL);
    }

    public List<String> getAdmins() {
       return getUsersByRole(getAdminsSQL);
    }
}
