package com.paralli.phantom_waypoints;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;


@SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
public class Command_waypoint implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("teleport")) {
                    TextComponent message = new TextComponent("");
                    message.addExtra(Tag.tag());

                    //Check for waypoint name to be given in command
                    if (args.length > 1) {

                        //retrieve list of waypoints from the waypoint data class
                        List<waypoint> waypoints = waypointData.readStoredJSON();

                        //build the name we are looking for. If the number of args is greater or equal to 2, we need to search for a multi worded name
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
                        player.sendMessage(name.toString());

                        //Search out list of waypoints for the given waypoint.
                        for (waypoint value : waypoints) {
                            //if the waypoint name matches our search, tp our player
                            if (value.name.equalsIgnoreCase(name.toString())) {

                                //if the config file has the take Exp value set to true check for Exp level balance and subtract if enough. Error if not
                                if(Main.config.getBoolean("teleportCostExp")) {
                                    if(player.getGameMode() == GameMode.CREATIVE){
                                        //Nothing. There is likely a better way to do this, so this statement is a beautiful candidate for a rewrite, but for now it functions :)
                                    } else if(player.getLevel() >= Main.config.getInt("teleportCostAmmount")){
                                        player.setLevel(player.getLevel() - Main.config.getInt("teleportCostAmmount"));
                                    } else {
                                        message.addExtra("You don't have enough Exp levels!\n[Cost: "+Main.config.getInt("teleportCostAmmount")+"]");
                                        player.spigot().sendMessage(message);
                                        return true;
                                    }
                                }

                                //Do the actual teleporting
                                message.addExtra("Teleporting...");
                                player.spigot().sendMessage(message);
                                Location loc = new Location(Bukkit.getServer().getWorld(value.world), value.x, value.y, value.z, value.yaw, value.pitch);
                                player.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 3.0F, 0.5F);
                                player.teleport(loc);
                                return true;
                            }
                        }
                        //Return a message if the given name couldnt be found in the system
                        message.addExtra("That waypoint doesn't seem to exist.");

                    } else {
                        message.addExtra("Please enter the name of the waypoint you would like to visit.");
                    }
                    player.spigot().sendMessage(message);
                    return true;
                }

                if(args[0].equalsIgnoreCase("add")){

                    if(!player.isOp()){
                        TextComponent message = new TextComponent("");
                        message.addExtra(Tag.tag());
                        message.addExtra("This command is reserved for server operators only.");
                        player.spigot().sendMessage(message);
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

                        if(!waypointData.addNew(created)){
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

                if (args[0].equalsIgnoreCase("remove")) {
                    if(!player.isOp()){
                        TextComponent message = new TextComponent("");
                        message.addExtra(Tag.tag());
                        message.addExtra("This command is reserved for server operators only.");
                        player.spigot().sendMessage(message);
                        return true;
                    }

                    TextComponent message = new TextComponent("");
                    message.addExtra(Tag.tag());
                    if (args.length > 1) {
                        int result = waypointData.removeWaypoint(args[1]);
                        if(result == 1){
                            message.addExtra("Waypoint removed.");
                        } else if(result == 0) {
                            message.addExtra("That Waypoint doesn't seem to exist.");
                        } else if(result == 2) {
                            message.addExtra("Something went wrong ;(");
                        }
                    } else {
                        message.addExtra("Please give the name of the waypoint to remove.");
                    }
                    player.spigot().sendMessage(message);
                    return true;
                }
            }
            else {
                TextComponent message = new TextComponent();
                message.addExtra(Tag.tag());
                message.addExtra("Known Waypoints: ");
                message.addExtra("\n");

                List<waypoint> waypoints = waypointData.readStoredJSON();

                for (waypoint value : waypoints) {
                    TextComponent comp = new TextComponent("[");
                    TextComponent Link = new TextComponent(value.name);
                    Link.setColor(ChatColor.GREEN);
                    comp.addExtra(Link);
                    comp.addExtra("]");
                    comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoint teleport " + value.name));
                    message.addExtra(comp);
                    message.addExtra(" ");
                }

                player.spigot().sendMessage(message);
                return true;
            }
            return false;
        }
        return false;
    }
}
