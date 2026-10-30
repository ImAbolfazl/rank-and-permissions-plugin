
package me.abolfazl.ranksandpermissions;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private Connection connection;

    public void connect(JavaPlugin plugin) throws SQLException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        File database = new File(plugin.getDataFolder(), "ranks.db");

        DriverManager.registerDriver(new org.sqlite.JDBC());

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + database.getAbsolutePath()
        );

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS player_ranks (" +
                            "uuid TEXT PRIMARY KEY, " +
                            "rank TEXT)"
            );
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS ranks (" +
                            "name TEXT PRIMARY KEY, " +
                            "prefix TEXT NOT NULL, " +
                            "isDefault BOOLEAN DEFAULT 0)");
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
