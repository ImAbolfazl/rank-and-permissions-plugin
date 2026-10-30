package me.abolfazl.ranksandpermissions.listeners;

import me.abolfazl.ranksandpermissions.RanksAndPermissions;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private RanksAndPermissions plugin;

    public ChatListener(RanksAndPermissions plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){
        String uuid =  event.getPlayer().getUniqueId().toString();
        String prefix = plugin.prefixCache.get(uuid);

        if(prefix == null){
            prefix = "";
        }

        String message = prefix + " " + event.getPlayer().getName() + " &6» &f" + event.getMessage();
        event.setFormat(ChatColor.translateAlternateColorCodes('&', message));
    }
}
