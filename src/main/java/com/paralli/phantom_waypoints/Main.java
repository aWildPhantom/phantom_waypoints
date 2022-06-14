package com.paralli.phantom_waypoints;

import com.paralli.phantom_waypoints.commands.admin;
import com.paralli.phantom_waypoints.listeners.waypointDamageListener;
import com.paralli.phantom_waypoints.listeners.waypointInteractListener;
import com.paralli.phantom_waypoints.models.wayplayer;
import com.paralli.phantom_waypoints.models.waypoints;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main extends JavaPlugin {
    public static Main instance;



    public static List<waypoints> waypoint_list;
    public static List<wayplayer> wayplayer_list;

    @Override
    public void onEnable() {
        waypoint_list = new ArrayList<waypoints>();
        wayplayer_list = new ArrayList<wayplayer>();

        //check our plugin data folder exists
        try {
            this.getDataFolder().mkdir();
        } catch (Exception e) {
            e.printStackTrace();
        }


        this.getLogger().info("Hello :)");
        instance = this;

        //TODO: Add method to pull waypoint_list from file storage




        //TODO: Add method to pull discovered waypoints from file storage

        //register commands
        this.getCommand("padmin").setExecutor(new admin());

        //register listeners
        getServer().getPluginManager().registerEvents(new waypointDamageListener(), this);
        getServer().getPluginManager().registerEvents(new waypointInteractListener(), this);


    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        this.getLogger().info("Bye Bye :)");

    }

    public static Main getPlugin() {
        return instance;
    }
}
