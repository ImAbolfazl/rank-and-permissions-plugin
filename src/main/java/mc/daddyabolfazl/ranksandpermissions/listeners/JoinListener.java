package mc.daddyabolfazl.ranksandpermissions.listeners;

import mc.daddyabolfazl.ranksandpermissions.RanksAndPermission;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

public class JoinListener implements Listener {
    private RanksAndPermission plugin;

    public JoinListener(RanksAndPermission plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        try{
            String uuid = event.getPlayer().getUniqueId().toString();
            String rank = plugin.getRankManager().getRank(uuid);
            if(rank == null){
                rank = plugin.getRankManager().getDefaultRank();
                plugin.getRankManager().addRank(uuid, rank);
            }
            String prefix = plugin.getRankManager().getPrefix(rank);
            plugin.prefixCache.put(uuid, prefix);
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        plugin.prefixCache.remove(uuid);
    }
}
