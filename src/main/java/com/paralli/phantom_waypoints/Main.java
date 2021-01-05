package com.paralli.phantom_waypoints;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class Main extends JavaPlugin {

    public static String pluginPath;
    public static Logger console;
    public static FileConfiguration config;


    @Override
    public void onEnable() {
        this.console = getLogger();
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
        console.info("Grabbing data folder path...");
        getDataFolder().mkdir();
        this.pluginPath = getDataFolder().getAbsolutePath();
        console.info("Got it! Now to load the config file...");
        saveDefaultConfig();
        this.config = this.getConfig();
        console.info("Presto! Now for the commands...");

        //Register commands
        try {
            this.getCommand("waypoint").setExecutor(new Command_waypoint());
        } catch (Exception e) {
            console.info("An error has occurred while loading the required commands. \n"+"Please send the following to the plugin author:");
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
