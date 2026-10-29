package mc.daddyabolfazl.ranksandpermissions.commands;

import mc.daddyabolfazl.ranksandpermissions.RanksAndPermission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.UUID;

public class RankCommand implements CommandExecutor {

    private RanksAndPermission plugin;

    public RankCommand(RanksAndPermission plugin) {
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
                    "/rank default <rank>"
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
                    player.sendMessage("created successfully " + args[2]);
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
                UUID uuid = Bukkit.getPlayer(args[1]).getUniqueId();
                String prefix = plugin.getRankManager().getPrefix(args[2]);
                if(prefix == null){
                    player.sendMessage(args[2] + " does not exist!");
                    return true;
                }
                boolean added = plugin.getRankManager().addRank(uuid.toString(), args[2]);

                if(added){
                    player.sendMessage("added " + args[2] + " to " + Bukkit.getPlayer(args[1]).getName());
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

        return true;
    }
}
