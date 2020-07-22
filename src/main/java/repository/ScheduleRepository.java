package repository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleRepository extends AbstractRepository {
    private static final String getSQL = "SELECT * FROM SCHEDULE WHERE USER_ID = ?";
    private static final String checkSQL = "SELECT * FROM SCHEDULE WHERE HOUR = ? AND MINUTES = ?";
    private static final String clearSQL = "DELETE FROM SCHEDULE WHERE USER_ID = ?";
    private static final String addSQL = "INSERT INTO SCHEDULE (USER_ID, HOUR, MINUTES) VALUES (?, ?, ?)";

    public void addSchedule(String chatId, int hour, int minutes) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(addSQL);
             PreparedStatement stmtCheck = connection.prepareStatement(checkSQL)) {

            stmtCheck.setInt(1, hour);
            stmtCheck.setInt(2, minutes);

            ResultSet rs = stmtCheck.executeQuery();

            while (rs.next()) {
                if (rs.getString("user_id").equals(chatId)) {
                    throw new IllegalArgumentException("This time already exists");
                }
            }

            stmt.setString(1, chatId);
            stmt.setInt(2, hour);
            stmt.setInt(3, minutes);
            stmt.executeUpdate();

        } catch (Exception e) {
            log.error("Exception while trying to add schedule to user {}: {}",
                    chatId, e.getMessage());
        }
    }

    public Map<Integer, List<Integer>> getSchedule(String chatId) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(getSQL,
                     ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, chatId);

            ResultSet rs = stmt.executeQuery();
            Map<Integer, List<Integer>> result = new HashMap<>();

            while (rs.next()) {
                int hour = rs.getInt("hour");
                int minute = rs.getInt("minutes");

                result.computeIfAbsent(hour, k -> new ArrayList<>()).add(minute);
            }

            return result;
        } catch (Exception e) {
            log.error("Exception while trying to get schedule of user {}: {}",
                    chatId, e.getMessage());
            return null;
        }
    }

    public int getScheduleSize(String chatId) {
        int rowcount = 0;

        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(getSQL,
                     ResultSet.TYPE_SCROLL_SENSITIVE,
                     ResultSet.CONCUR_UPDATABLE)) {
            stmt.setString(1, chatId);

            ResultSet rs = stmt.executeQuery();

            if (rs.last()) {
                rowcount = rs.getRow();
                rs.beforeFirst();
            }

        } catch (Exception e) {
            log.error("Exception while trying to get schedule of user {}: {}",
                    chatId, e.getMessage());
        }
        return rowcount;
    }

    public void clearSchedule(String chatId) {
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(clearSQL)) {
            stmt.setString(1, chatId);
            stmt.executeUpdate();
        } catch (Exception e) {
            log.error("Exception while trying to delete schedule of user {}: {}",
                    chatId, e.getMessage());
        }
    }

    public List<String> checkSchedule(LocalDateTime ldt) {
        List<String> result = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(checkSQL)) {
            stmt.setInt(1, ldt.getHour());
            stmt.setInt(2, ldt.getMinute());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                result.add(rs.getString("user_id"));
            }
        } catch (Exception e) {
            log.error("Exception while trying to check schedule: {}",
                    e.getMessage());
        }

        return result;
    }
}
