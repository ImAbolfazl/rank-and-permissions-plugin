
package mc.daddyabolfazl.ranksandpermissions;

import mc.daddyabolfazl.ranksandpermissions.commands.RankCommand;
import mc.daddyabolfazl.ranksandpermissions.listeners.ChatListener;
import mc.daddyabolfazl.ranksandpermissions.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.SQLException;
import java.util.HashMap;

public final class RanksAndPermission extends JavaPlugin {

    private DatabaseManager databaseManager;
    private RankManager rankManager;

    public HashMap<String, String> prefixCache =  new HashMap<>();

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

    public String getPrefix(String uuid){
        return prefixCache.get(uuid);
    }
}
