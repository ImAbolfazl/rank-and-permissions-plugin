
package me.abolfazl.ranksandpermissions;

import me.abolfazl.ranksandpermissions.commands.RankCommand;
import me.abolfazl.ranksandpermissions.listeners.ChatListener;
import me.abolfazl.ranksandpermissions.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;
import java.util.HashMap;

public final class RanksAndPermissions extends JavaPlugin {

    private DatabaseManager databaseManager;
    private RankManager rankManager;
    private PermissionManager permissionManager;

    public HashMap<String, String> prefixCache =  new HashMap<>();

    public String[] permissions = {
        "fly",
        "gamemode",
        "vanish"
    };

    @Override
    public void onEnable() {
        databaseManager = new DatabaseManager();

        try {
            databaseManager.connect(this);
            getLogger().info("Database connected successfully");
        } catch (SQLException e) {
            getLogger().severe("Could not connect to SQLite!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        rankManager = new RankManager(databaseManager);
        permissionManager = new PermissionManager(databaseManager);
        getCommand("rank").setExecutor(new RankCommand(this));
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        System.out.println("Ranks and Permissions have been enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            try {
                databaseManager.disconnect();
            } catch (SQLException e) {
                getLogger().severe("Could not disconnect from SQLite!");
                e.printStackTrace();
            }
        }
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public PermissionManager getPermissionManager(){
        return permissionManager;
    }

    public String getPrefix(String uuid){
        return prefixCache.get(uuid);
    }

    public boolean hasPermission(String uuid, String permission){
        try{
            String rank = rankManager.getRank(uuid);
            return permissionManager.hasPermission(rank, permission);
        }catch(SQLException e){
            System.out.println(e);
            return false;
        }
    }
}
