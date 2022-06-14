package com.paralli.phantom_waypoints.commands;

import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.helpers.storage;
import com.paralli.phantom_waypoints.models.waypoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;


import java.awt.*;
import java.util.UUID;
import java.util.Vector;

public class admin implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(!(commandSender instanceof Player)) {
            return true;
        }

        Player p = (Player) commandSender;

        //Check that our user has the proper permissions
        if(!(p.hasPermission("pwaypoint.admin"))){
            p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] You dont have the proper permissions to use this function!");
            return true;
        }


        if(!(strings.length > 0)) {
            p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] Please provide arguments!");
            return true;
        }

        if(strings[0].equalsIgnoreCase("create")){
            if(create(strings, p)){
                return true;
            } else {
                p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] There was an error creating the waypoint. Please check your logs");
            }
        }

        if(strings[0].equalsIgnoreCase("remove")){
            if(remove(strings, p)){
                return true;
            }

            p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] There was an error removing the waypoint. Please check your logs.");
        }

        if(strings[0].equalsIgnoreCase("save")){
            save();
            return true;
        }
        if(strings[0].equalsIgnoreCase("load")){
            load();
            return true;
        }


        return false;
    }


    private boolean create(String[] strings, Player p){
        String name = concat_name(strings, p);

        Main.getPlugin().getLogger().info("[DEBUG] NAME:"+name+" SENDER: "+p.getName());

        //calculate offset to put players 1 block in front of the waypoint. Based on commanding player
        int x = 0;
        int z = 0;
        BlockFace facing = p.getFacing();

        if (facing.equals(BlockFace.NORTH)){
            facing = BlockFace.SOUTH;
            z = -1;
        }

        if(facing.equals(BlockFace.SOUTH)){
            facing = BlockFace.NORTH;
            z = 1;
        }

        if(facing.equals(BlockFace.EAST)){
            facing = BlockFace.WEST;
            x = 1;
        }

        if (facing.equals(BlockFace.WEST)){
            facing = BlockFace.EAST;
            x = -1;
        }

        //create the entity in world

        Location loc = p.getLocation();
        loc.setX(loc.getBlockX()+0.5);
        loc.setY(loc.getBlockY());
        loc.setZ(loc.getBlockZ()+0.5);

        //First we should check if there exists a waypoint by that name already.
        for(int c = 0; c < Main.waypoint_list.size(); c++){
            if(name.equalsIgnoreCase(ChatColor.stripColor(Main.waypoint_list.get(c).name))){
                p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] A Waypoint with that name already exists!");
                return true;
            }
        }



        EnderCrystal toSpawn = (EnderCrystal) p.getWorld().spawnEntity(loc.add(0,0.5,0), EntityType.ENDER_CRYSTAL);
        toSpawn.setMetadata("pwaypoint", new FixedMetadataValue(Main.getPlugin(), name));
        toSpawn.setCustomName(ChatColor.translateAlternateColorCodes('&', "&5&l"+name));
        toSpawn.setCustomNameVisible(true);
        toSpawn.setPersistent(true);
        toSpawn.setShowingBottom(false);

        //create Waypoint object for us to store
        waypoints waypoint = new waypoints();
        waypoint.UUID = toSpawn.getUniqueId();
        waypoint.name = name;

        waypoint.xcoord = toSpawn.getLocation().getBlockX();
        waypoint.ycoord = toSpawn.getLocation().getBlockY();
        waypoint.zcoord = toSpawn.getLocation().getBlockZ();
        waypoint.worldName = toSpawn.getLocation().getWorld().getName();

        waypoint.offsetX = x;
        waypoint.offsetZ = z;
        waypoint.facing = facing;

        waypoint.chunk = toSpawn.getLocation().getChunk();

        //add it to the global list
        Main.waypoint_list.add(waypoint);

        p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] Created the waypoint!");
        return true;
    }

    private boolean remove(String[] strings, Player p){
        String name = concat_name(strings, p);
        if(name == null){
            return false;
        }

        //search waypoint list for the waypoint in question
        for(int i = 0; i < Main.waypoint_list.size(); i++){
            p.sendMessage("[DEBUG] Searching: "+ Main.waypoint_list.get(i).UUID);
            if(ChatColor.stripColor(Main.waypoint_list.get(i).name).equals(name)){
                //grab UUID for later use in player list
                UUID temp = Main.waypoint_list.get(i).UUID;

                //check if we need to go through the process of force loading the chunk
                if(!(Main.waypoint_list.get(i).chunk.isLoaded())) {
                    //If the chunk is unloaded, we need to force load the chunk to be able to remove the entity.
                    Main.waypoint_list.get(i).chunk.load();
                    final int finalI = i;
                    final Player pfin = p;
                    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            //remove entity while chunk is loaded. Add a slight delay for the server to process the entity
                            try {
                                Bukkit.getServer().getEntity(Main.waypoint_list.get(finalI).UUID).remove();
                            } catch (Exception e) {
                                Main.getPlugin().getLogger().info(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] There was a problem! Please report this to @aWildPhantom!");
                                e.printStackTrace();
                                return;
                            }

                            if(removeFromPlayers(Main.waypoint_list.get(finalI), pfin)){
                                Main.waypoint_list.remove(finalI);
                                pfin.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] Removed the waypoint!");
                            } else {
                                pfin.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] There was a problem! Please check the console for clues!");
                            }
                        }
                    }, 5L);
                } else {
                    removeFromPlayers(Main.waypoint_list.get(i), p);
                    Bukkit.getServer().getEntity(Main.waypoint_list.get(i).UUID).remove();
                    Main.waypoint_list.remove(i);
                    p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] Removed the waypoint!");
                }
                return true;
            }
        }

        p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] No waypoint with that name exists!");
        return true;
    }

    private String concat_name(String[] strings, Player p){
        // check that we have enough args to properly create the waypoint.
        if(!(strings.length > 1)) {
            p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] Please provide a name for the waypoint.");
            return null;
        }

        //check if we have a multi part name. If so, concat into one easy name. Else, just set the name as needed.;
        String name;
        if(strings.length > 2) {
            //concat all the strings after the create keyword to make one coherent name for the waypoint.
            StringBuilder namebuilder = new StringBuilder();
            for (int i = 1; i < strings.length; i++) {
                namebuilder.append(strings[i]);
                namebuilder.append(" ");
            }
            name = namebuilder.toString().trim();
        } else {
            name = strings[1];
        }
        return name;
    }

    private boolean removeFromPlayers(waypoints waypoint, Player p){
        //remove from player's known list
        UUID temp = waypoint.UUID;

        for(int x = 0; x < Main.wayplayer_list.size(); x++){
            for(int z = 0; z < Main.wayplayer_list.get(x).known.size(); z++){
                if(temp.equals(Main.wayplayer_list.get(x).known.get(z).UUID)){
                    Main.wayplayer_list.get(x).known.remove(z);
                    return true;
                }
            }
        }
        return false;
    }

    private void save(){
        storage.savePlayerData();
        storage.saveWaypointData();
    }

    private void load(){
        Main.waypoint_list = storage.retrieveWaypointData();
        for(int i=0; i<Main.waypoint_list.size();i++){
            Main.getPlugin().getLogger().info(String.valueOf(Main.waypoint_list.get(i).UUID));
        }
    }


}

