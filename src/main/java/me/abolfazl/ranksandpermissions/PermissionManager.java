package me.abolfazl.ranksandpermissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PermissionManager {
    
    private DatabaseManager databaseManager;

    public PermissionManager(DatabaseManager databaseManager){
        this.databaseManager = databaseManager;
    }

    public boolean addPermission(String rank, String permission) throws SQLException{
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("INSERT INTO permissions (rank, permission) VALUES (?, ?)")){
            statement.setString(1, rank);
            statement.setString(2, permission);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean deletePermission(String rank, String permission) throws SQLException{
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("DELETE FROM permissions WHERE rank = ? AND permission = ?")){
            statement.setString(1, rank);
            statement.setString(2, permission);
            return statement.executeUpdate() > 0;
        }
    }

    public boolean hasPermission(String rank, String permission) throws SQLException{
        try(PreparedStatement statement = databaseManager.getConnection().prepareStatement("SELECT * FROM permissions WHERE rank = ? AND permission = ?")){
            statement.setString(1, rank);
            statement.setString(2, permission);
            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        }
    }
}
