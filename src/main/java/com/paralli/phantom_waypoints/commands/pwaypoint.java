package com.paralli.phantom_waypoints.commands;

import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.functions.waypointData;
import com.paralli.phantom_waypoints.functions.waypointFunctions;
import com.paralli.phantom_waypoints.models.Tag;
import com.paralli.phantom_waypoints.models.waypoint;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class pwaypoint implements CommandExecutor {

    private final int XPCost;

    public pwaypoint() {
        Main plugin = Main.getPlugin();
        XPCost = plugin.getConfig().getInt("ExpCost");
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            //retrieve player that sent the command.
            Player player = (Player) commandSender;

            //verify we have some form of arguments since this is a nested command.
            if(!(args.length > 0)){
                return false;
            }

            if(args[0].equalsIgnoreCase("teleport")) {
                return teleport(player, args);
            }

            if(args[0].equalsIgnoreCase("add")){
                return add(player, args);
            }

            if (args[0].equalsIgnoreCase("remove")) {
                return remove(player, args);
            }

            if (args[0].equalsIgnoreCase("reveal")) {
                return reveal(player);
            }

            if (args[0].equalsIgnoreCase("save")) {
                return forceSave(player);
            }
        }
    return false;
    }

    private boolean add(Player player, String[] args) {
        if(!(player.hasPermission("pwaypoint.admin"))){
            player.sendMessage("Nope");
            return true;
        }

            TextComponent message = new TextComponent("");
            message.addExtra(Tag.tag());
            if(args.length > 1){
                waypoint created = new waypoint();

                StringBuilder name = new StringBuilder();
                if(args.length >= 2) {
                    for (int i = 1; i < args.length; i++) {
                        name.append(args[i]);
                        if(i+1 < args.length){
                            name.append(" ");
                        }
                    }
                } else {
                    name.append(args[1]);
                }

                created.name = name.toString();

                Location loc = player.getLocation();
                created.world = loc.getWorld().getName();

                switch (String.valueOf(player.getFacing())) {
                    case "NORTH":
                        created.yaw = -180;
                        break;

                    case "SOUTH":
                        created.yaw = 0;
                        break;

                    case "WEST":
                        created.yaw = 90;
                        break;

                    case "EAST":
                        created.yaw = -90;
                        break;

                    default:
                        created.yaw = 0;
                }

                created.pitch = 0;

                created.x = loc.getBlockX()+0.5;
                created.y = loc.getBlockY();
                created.z = loc.getBlockZ()+0.5;

                if(waypointData.exists(created)){
                    message.addExtra("That waypoint already exists!");
                    player.spigot().sendMessage(message);
                    return true;
                }

                if(!waypointData.addNewWaypoint(created)){
                    message.addExtra("The waypoint could not be created.");
                    player.spigot().sendMessage(message);
                    return true;
                }
                message.addExtra("Waypoint "+created.name+" created!");
            } else {
                message.addExtra("a name is required to create the new waypoint!");
            }
            player.spigot().sendMessage(message);
            return true;
        }

    private boolean teleport(Player player, String[] args) {
           if(args.length >= 2){
                StringBuilder name;
                name = new StringBuilder();

                //if args is greater than 4, construct a singular string name for the waypoint
                if(args.length >= 3) {
                    for (int i = 1; i <= args.length-1; i++) {
                        name.append(args[i]);
                        if(i+1 <= args.length-1){
                            name.append(" ");
                        }
                    }
                } else {
                    name.append(args[1]);
                }

                //do the actual teleporting
                List<waypoint> waypointList = Main.globalWaypoints;
                waypoint search = null;

                //first search for the waypoint
                for(waypoint w: waypointList){
                    if(w.name.equalsIgnoreCase(name.toString())){
                        search = w;
                    }
                }

                if(!(search==null)){
                    Location check = player.getLocation();

                    Location loc = new Location(Bukkit.getServer().getWorld(search.world), search.x, search.y, search.z, search.yaw, search.pitch);

                    boolean cleared = false;
                    for(waypoint w: waypointList){
                        Location wcheck = new Location(Bukkit.getServer().getWorld(w.world), w.x, w.y, w.z);

                        if(wcheck.getWorld() != check.getWorld()){
                            continue;
                        }
                        if(wcheck.distance(check) < 2){
                            cleared = true;
                        }
                    }

                    if(!cleared){
                        waypointFunctions.sendMessage(player, "You can't teleport if you are not close to a waypoint!");
                        return true;
                    }

                    //check for xp cost
                    if(player.getLevel()-XPCost < 0) {
                        waypointFunctions.sendMessage(player, "You don't have enough xp! It costs "+XPCost+" levels to teleport.");
                        return true;
                    }

                    player.giveExpLevels(-1* XPCost);
                    player.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 3.0F, 0.5F);
                    player.teleport(loc);
                    return true;
                }
            }
        return false;
    }

    private boolean remove(Player player, String[] args) {
        if(!player.hasPermission("pwaypoint.admin")){
            waypointFunctions.sendMessage(player, "This command is reserved for operators only!");
            return true;
        }

        if (args.length > 1) {

            StringBuilder name = new StringBuilder();
            if(args.length >= 2) {
                for (int i = 1; i < args.length; i++) {
                    name.append(args[i]);
                    if(i+1 < args.length){
                        name.append(" ");
                    }
                }
            } else {
                name.append(args[1]);
            }

            int result = waypointData.removeWaypoint(name.toString());
            if(result == 1){
                waypointFunctions.sendMessage(player, "Waypoint removed");
            } else if(result == 0) {
                waypointFunctions.sendMessage(player, "That waypoint doesnt seem to exist!");
            } else if(result == 2) {
                waypointFunctions.sendMessage(player,"Something went wrong ;(");
            }
        } else {
            waypointFunctions.sendMessage(player, "Please give the name of the waypoint to remove.");
        }
        return true;
    }

    private boolean reveal(Player player) {
        if(player.hasPermission("pwaypoint.admin")){
            waypointFunctions.revealAll(player);
        } else {
            waypointFunctions.sendMessage(player,"This command is reserved for Admins only.");
        }
        return true;
    }

    private boolean forceSave(Player player) {
        if(!(player.hasPermission("pwaypoint.admin"))){
            waypointFunctions.sendMessage(player,"This command is reserved for Admins only.");
            return true;
        }


        if(waypointData.saveDataToFiles()){
            player.sendMessage("Successfully force saved waypoint data.");
            return true;
        }
        player.sendMessage("Something went wrong. Check console for more info.");
        return true;
    }
}