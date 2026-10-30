package me.abolfazl.ranksandpermissions.commands;

import me.abolfazl.ranksandpermissions.RanksAndPermissions;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.UUID;

public class RankCommand implements CommandExecutor {

    private RanksAndPermissions plugin;

    public RankCommand(RanksAndPermissions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = (Player) sender;
        if(!player.isOp()){
            player.sendMessage("only op can execute this command");
            return true;
        }

        if(args.length == 0){
            player.sendMessage(new String[]{
                    "Usage:",
                    "/rank create <name> <prefix>",
                    "/rank delete <name>",
                    "/rank update <name> <new prefix>",
                    "/rank add <player> <rank>",
                    "/rank remove <player>",
                    "/rank default <rank>",
                    "/rank permission list",
                    "/rank permission add <rank> <permission>",
                    "/rank permission delete <rank> <permission>"
            });
            return true;
        }

        if(args[0].equalsIgnoreCase("create")){
            if(args.length < 3){
                player.sendMessage("/rank create <name> <prefix>");
                return true;
            }

            try{
                String prefix = plugin.getRankManager().getPrefix(args[1]);
                if(prefix != null){
                    player.sendMessage(args[1] + " already exists!");
                    return true;
                }
            }catch(SQLException e){
                System.out.println(e.getMessage());
            }

            try{
                boolean created = plugin.getRankManager().createRank(args[1], args[2]);

                if(created){
                    player.sendMessage("successfully created " + args[2]);
                }else{
                    player.sendMessage("rank creation failed!");
                }
            }catch(SQLException e){
                player.sendMessage("something went wrong!");
                System.out.println(e.getMessage());
            }
        }

        if(args[0].equalsIgnoreCase("delete")){
            if(args.length < 2){
                player.sendMessage("/rank delete <name>");
                return true;
            }

            try{
                boolean deleted  = plugin.getRankManager().deleteRank(args[1]);

                if(deleted){
                    player.sendMessage("rank deleted successfully");
                }else{
                    player.sendMessage("rank delete failed!");
                }
            }catch(SQLException e){
                player.sendMessage("something went wrong!");
                System.out.println(e.getMessage());
            }
        }

        if(args[0].equalsIgnoreCase("update")){
            if(args.length < 3){
                player.sendMessage("/rank update <name> <new prefix>");
                return true;
            }

            try{
                boolean updated = plugin.getRankManager().updatePrefix(args[1], args[2]);

                if(updated){
                    player.sendMessage("prefix updated: " + args[2]);
                }else{
                    player.sendMessage("failed to update the prefix!");
                }
            }catch(SQLException e){
                player.sendMessage("something went wrong!");
                System.out.println(e.getMessage());
            }
        }

        if(args[0].equalsIgnoreCase("add")){
            if(args.length < 3){
                player.sendMessage("/rank add <player> <rank>");
                return true;
            }

            try{
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    player.sendMessage("player is not online!");
                    return true;
                }
                UUID uuid = target.getUniqueId();
                String prefix = plugin.getRankManager().getPrefix(args[2]);
                if(prefix == null){
                    player.sendMessage(args[2] + " does not exist!");
                    return true;
                }
                plugin.getRankManager().removeRank(uuid.toString());
                boolean added = plugin.getRankManager().addRank(uuid.toString(), args[2]);

                if(added){
                    player.sendMessage("added " + args[2] + " to " + Bukkit.getPlayer(args[1]).getName());
                    plugin.prefixCache.put(uuid.toString(), prefix);
                }else{
                    player.sendMessage("failed to add the rank to the database!");
                }
            }catch(SQLException e){
                player.sendMessage("something went wrong!");
                System.out.println(e.getMessage());
            }
        }

        if(args[0].equalsIgnoreCase("remove")){
            if(args.length < 2){
                player.sendMessage("/rank remove <player>");
                return true;
            }

            try{
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null){
                    player.sendMessage("player is not online!");
                    return true;
                }
                UUID uuid = Bukkit.getPlayer(args[1]).getUniqueId();
                boolean removed = plugin.getRankManager().removeRank(uuid.toString());

                if(removed){
                    player.sendMessage("removed " + Bukkit.getPlayer(args[1]).getName() + "'s rank!");
                    plugin.prefixCache.remove(uuid.toString());
                    plugin.prefixCache.put(uuid.toString(), plugin.getRankManager().getDefaultRank());
                }else{
                    player.sendMessage("failed to remove the rank from the database!");
                }
            }catch(SQLException e){
                player.sendMessage("something went wrong!");
                System.out.println(e.getMessage());
            }
        }

        if(args[0].equalsIgnoreCase("default")){
            if(args.length < 2){
                player.sendMessage("/rank default <player>");
            }

            try{
                boolean exists = plugin.getRankManager().getPrefix(args[1]) != null;
                if(!exists){
                    player.sendMessage(args[1] + " does not exist!");
                    return true;
                }

                boolean added = plugin.getRankManager().setDefaultRank(args[1]);

                if(added){
                    player.sendMessage(args[1] + " is now the default rank!");
                }else{
                    player.sendMessage("failed to remove the rank from the database!");
                }
            }catch(SQLException e){
                player.sendMessage("something went wrong!");
                System.out.println(e.getMessage());
            }
        }

        if(args[0].equalsIgnoreCase("permission")){
            if(args.length == 1){
                player.sendMessage(new String[]{
                    "/rank permission list",
                    "/rank permission add <rank> <permission>",
                    "/rank permission delete <rank> <permission>"
                });
                return true;
            }

            if(args[1].equalsIgnoreCase("list")){
                player.sendMessage(plugin.permissions);
                return true;
            }else if(args[1].equalsIgnoreCase("add")){
                if(args.length > 4){
                    player.sendMessage("/rank permission add <rank> <permission>");
                    return true;
                }
                if (!Arrays.asList(plugin.permissions).contains(args[3].toLowerCase())) {
                    player.sendMessage(new String[]{
                        args[3] + " is not a valid permission!",
                        "use /rank permission list"
                    });
                    return true;
                }
                try{
                    if(plugin.getRankManager().getPrefix(args[2].toLowerCase()) == null){
                        player.sendMessage(args[2] + " is not a valid rank!");
                    }

                    System.out.println("args[2]:" + args[2]);
                    System.out.println("args[3]:" + args[3]);
                    boolean added = plugin.getPermissionManager().addPermission(args[2].toLowerCase(), args[3].toLowerCase());

                    if(added){
                        player.sendMessage("permissions set!");
                    }else{
                        player.sendMessage("failed to set the permission in the database!");
                    }
                }catch(SQLException e){
                    player.sendMessage("something went wrong!");
                    System.out.println(e.getMessage());
                }
            }else if(args[1].equalsIgnoreCase("delete")){
                if(args.length > 4){
                    player.sendMessage("/rank permission delete <rank> <permission>");
                    return true;
                }
                if (!Arrays.asList(plugin.permissions).contains(args[3].toLowerCase())) {
                    player.sendMessage(new String[]{
                        args[3] + " is not a valid permission!",
                        "use /rank permission list"
                    });
                    return true;
                }
                try{
                    if(plugin.getRankManager().getPrefix(args[2].toLowerCase()) == null){
                        player.sendMessage(args[2] + " is not a valid rank!");
                    }

                    boolean deleted = plugin.getPermissionManager().addPermission(args[2].toLowerCase(), args[3].toLowerCase());

                    if(deleted){
                        player.sendMessage("permissions deleted!");
                    }else{
                        player.sendMessage("failed to delete the permission from the database!");
                    }
                }catch(SQLException e){
                    player.sendMessage("something went wrong!");
                    System.out.println(e.getMessage());
                }
            }else{
                player.sendMessage(new String[]{
                    "/rank permission list",
                    "/rank permission add <rank> <permission>",
                    "/rank permission delete <rank> <permission>"
                });
                return true;
            }
        }

        return true;
    }
}
