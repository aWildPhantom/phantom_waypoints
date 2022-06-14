package com.paralli.phantom_waypoints.helpers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.models.wayplayer;
import com.paralli.phantom_waypoints.models.waypoints;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;


import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class storage {


    public static List<waypoints> retrieveWaypointData() {
        String path = Main.getPlugin().getDataFolder().getAbsolutePath() + "/waydata.json";
        File f = new File(path);
        if (f.exists()){
            // pull the data
            //Init Gson object
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String fileContents = "";
            try {
                Scanner reader = new Scanner(f);
                if (reader.hasNext()) {
                    //read until end of file marker
                    fileContents = reader.useDelimiter("\\z").next();
                } else {
                    fileContents = null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            Type waypointtype = new TypeToken<ArrayList<waypoints>>(){}.getType();

            //if the file contents could be read, go through the loading process,
            //else just return the one already generated
            if(fileContents != null) {
                List<waypoints> waypoints = gson.fromJson(fileContents, waypointtype);

                //rebuild chunk info since chunk data can't be transfered into a json format for saving.
                for (int i = 0; i < waypoints.size(); i++) {
                    String worldname = waypoints.get(i).worldName;
                    World world = Bukkit.getWorld(worldname);
                    Location loc = new Location(world, waypoints.get(i).xcoord, waypoints.get(i).ycoord, waypoints.get(i).zcoord);
                    waypoints.get(i).chunk = loc.getChunk();
                }


                return waypoints;
            }

            return Main.waypoint_list;

            
        } else {
            //create new file if it doesnt already exists.
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void retrievePlayerData() {

    }

    public static void saveWaypointData() {
        //Init Gson object
        String path = Main.getPlugin().getDataFolder().getAbsolutePath() + "/waydata.json";
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Transform our waypoints to a json string
        List<waypoints> waypoints = Main.waypoint_list;
        String json = gson.toJson(waypoints);

        try {
            //save it to a file
            File f = new File(path);

            //check for our file to make sure it exists
            f.createNewFile();

            FileWriter fw = new FileWriter(path);
            fw.write(json);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void savePlayerData() {
        //Init Gson object
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //convert it to json
        List<wayplayer> wayplayers = Main.wayplayer_list;
        String json = gson.toJson(wayplayers);

        try {
            //save it to a file
            String path = Main.getPlugin().getDataFolder().getAbsolutePath() + "/playerdata.json";
            File f = new File(path);

            //check for our file to make sure it exists
            f.createNewFile();

            FileWriter fw = new FileWriter(path);
            fw.write(json);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
