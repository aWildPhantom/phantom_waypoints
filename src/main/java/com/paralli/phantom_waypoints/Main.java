package com.paralli.phantom_waypoints;

import com.paralli.phantom_waypoints.functions.waypointData;
import com.paralli.phantom_waypoints.models.Player_waypoint;
import com.paralli.phantom_waypoints.models.waypoint;
import com.paralli.phantom_waypoints.functions.handleMoveEvent;
import com.paralli.phantom_waypoints.commands.pwaypoint;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

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

    @Override
    public void onEnable() {
        console = getLogger();
        console.info("\n" +
                "       ____  __                __                  \n" +
                "      / __ \\/ /_  ____ _____  / /_____  ____ ___   \n" +
                "     / /_/ / __ \\/ __ `/ __ \\/ __/ __ \\/ __ `__ \\  \n" +
                "    / ____/ / / / /_/ / / / / /_/ /_/ / / / / / /  \n" +
                " _ /_/   / / /_/\\__,_/_/ /_/\\__/\\____/_/ /_/_/_/   \n" +
                "| |     / /___ ___  ______  ____  (_)___  / /______\n" +
                "| | /| / / __ `/ / / / __ \\/ __ \\/ / __ \\/ __/ ___/\n" +
                "| |/ |/ / /_/ / /_/ / /_/ / /_/ / / / / / /_(__  ) \n" +
                "|__/|__/\\__,_/\\__, / .___/\\____/_/_/ /_/\\__/____/  \n" +
                "             /____/_/                              \n" +
                "Built by @aWildPhantom\n");


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

        //register a scheduled event to save waypoint data to files every 10 minutes
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {saveDataToFiles();}
                }, 12000L, 12000L);


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
        console.info("Phantom Waypoints is shutting down! One second please... \n");
        //add any needed shutdown procedures here
        console.info("Done :)\n");
    }
}
