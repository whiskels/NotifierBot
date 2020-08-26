package repository;

import model.User;
import exception.IllegalUserException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository extends AbstractRepository {
    private static final String GET = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String GET_ADMINS = "SELECT * FROM USERS WHERE ADMIN = TRUE";
    private static final String GET_USERS = "SELECT * FROM USERS WHERE MANAGER = TRUE";

    public void addUser(String chatId) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement("INSERT INTO USERS " +
                     "(user_id, name, manager, admin, head) VALUES (?, ?, false, false, false)")) {
            stmt.setString(1, chatId);
            stmt.setString(2, chatId);

            stmt.execute();
        } catch (Exception e) {
            log.error("Exception while trying to add user {}: {}",
                    chatId, e.getMessage());
        }
    }


    public User getUser(String chatId) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(GET)) {
            stmt.setString(1, chatId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!isResultSetEmpty(rs)) {
                    rs.next();

                    return new User(rs.getString("user_id"),
                            rs.getString("name"),
                            rs.getBoolean("manager"),
                            rs.getBoolean("head"),
                            rs.getBoolean("admin"));

                } else {
                    throw new IllegalUserException();
                }
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

            rs.close();
        } catch (Exception e) {
            log.error("Exception while trying to get users: {}",
                    e.getMessage());
        }
        return result;
    }

    public List<String> getUsers() {
        return getUsersByRole(GET_USERS);
    }

    public List<String> getAdmins() {
        return getUsersByRole(GET_ADMINS);
    }

    public static boolean isResultSetEmpty(ResultSet rs) throws SQLException {
        return (!rs.isBeforeFirst() && rs.getRow() == 0);
    }
}
