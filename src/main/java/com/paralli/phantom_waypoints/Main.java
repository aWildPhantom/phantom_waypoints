package com.paralli.phantom_waypoints;

import com.paralli.phantom_waypoints.functions.waypointData;
import com.paralli.phantom_waypoints.models.Player_waypoint;
import com.paralli.phantom_waypoints.models.waypoint;
import com.paralli.phantom_waypoints.functions.handleMoveEvent;
import com.paralli.phantom_waypoints.commands.pwaypoint;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;
import java.util.logging.Logger;

import static com.paralli.phantom_waypoints.functions.waypointData.saveDataToFiles;

public class Main extends JavaPlugin {

    public static String pluginPath;
    public static Logger console;
    public static FileConfiguration config;

    //keep instances of the waypoint data and the player data in ram since they wont be changed often and arent really that big.
    public static List<waypoint> globalWaypoints;
    public static List<Player_waypoint> globalPlayerData;

    private BukkitTask saveTask;

    @Override
    public void onEnable() {
        console = getLogger();
        //add any needed stuff to init the waypoint system here.

        //get data folder path;
        console.info("Grabbing data folder path...");
        getDataFolder().mkdir();
        pluginPath = getDataFolder().getAbsolutePath();

        //load config
        console.info("Got it! Now to load the config file...");
        saveDefaultConfig();
        //set config variable so we can get it from other classes
        config = this.getConfig();
        console.info("Presto! Now to go ahead and read our waypoint list... ");
        globalWaypoints = com.paralli.phantom_waypoints.functions.waypointData.readStoredJSON();
        console.info("Okay. Just a few more steps...");
        globalPlayerData = com.paralli.phantom_waypoints.functions.waypointData.readPlayerData();


        long saveDelay = (long) this.getConfig().getInt("AutoSave") *60*20;
        //register a scheduled event to save waypoint data to files every 10 minutes
        saveTask = new BukkitRunnable() {
            public void run() {
                waypointData.saveDataToFiles();
            }
        }.runTaskTimer(this, saveDelay, saveDelay);
        //^ Above line pulls from config the default auto-save delay.


        console.info("Finishing final prep steps");


        //Register commands
        try {
            this.getCommand("pwaypoint").setExecutor(new pwaypoint());
        } catch (Exception e) {
            console.info("An error has occurred while loading the required commands. \n"+"Please send the following to the plugin author:");
            e.printStackTrace();
            this.onDisable();
        }

        //register listener
        try {
            getServer().getPluginManager().registerEvents(new handleMoveEvent(), this);
        } catch (Exception e) {
            console.info("An error has occurred while registering listeners. \n"+"Please send the following to the plugin author:");
            e.printStackTrace();
            this.onDisable();
        }

        console.info("Bingo! We are cleared for launch!");
    }

    public void onDisable() {
        console.info("Phantom Waypoints is shutting down! One second please...");

        //release the scheduled task so we don't have any weird behavior later
        saveTask.cancel();

        // save data to files
        waypointData.saveDataToFiles();

        //unregister moveEvent listenter
        HandlerList.unregisterAll(this);

        //add any needed shutdown procedures here
        console.info("Done :)\n");
    }

    public static Main getPlugin() {
        return this;
    }
}
