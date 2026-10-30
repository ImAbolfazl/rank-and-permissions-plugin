
package me.abolfazl.ranksandpermissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RankManager {

    private final DatabaseManager databaseManager;

    public RankManager(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean createRank(String name, String prefix) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("INSERT INTO ranks (name, prefix) VALUES (?, ?)")){
            statement.setString(1, name);
            statement.setString(2, prefix);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteRank(String name) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("DELETE FROM player_ranks WHERE rank = ?")) {
            statement.setString(1, name);
            statement.executeUpdate();
        }

        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("DELETE FROM ranks WHERE name = ?")){
            statement.setString(1, name);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean updatePrefix(String name, String prefix) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("UPDATE ranks SET prefix = ? WHERE name = ?")){
            statement.setString(1, prefix);
            statement.setString(2, name);

            return statement.executeUpdate() > 0;
        }
    }

    public String getPrefix(String name) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("SELECT * FROM ranks WHERE name = ?")){
            statement.setString(1, name);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString("prefix") : null;
            }
        }
    }

    public boolean addRank(String uuid, String rank) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("DELETE FROM player_ranks WHERE uuid = ?")){
            statement.setString(1, uuid);
            statement.executeUpdate();
        }
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("INSERT INTO player_ranks (uuid, rank) VALUES (?, ?)")){
            statement.setString(1, uuid);
            statement.setString(2, rank);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean removeRank(String uuid) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("DELETE FROM player_ranks WHERE uuid = ?")){
            statement.setString(1, uuid);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean setDefaultRank(String name) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("UPDATE ranks SET isDefault = 0")){
            statement.executeUpdate();
        }

        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement( "UPDATE ranks SET isDefault = 1 WHERE name = ?")){
            statement.setString(1, name);

            return statement.executeUpdate() > 0;
        }
    }

    public String getDefaultRank() throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("SELECT name FROM ranks WHERE isDefault = 1")){
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString("name") : null;
            }
        }
    }

    public String getRank(String uuid) throws SQLException {
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("SELECT rank FROM player_ranks WHERE uuid = ?")){
            statement.setString(1, uuid);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getString("rank") : null;
            }
        }
    }
}
