package com.paralli.phantom_waypoints.functions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.models.Player_waypoint;
import com.paralli.phantom_waypoints.models.waypoint;
import org.bukkit.entity.Player;


import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class waypointData {

    private static final String playerDataPath = Main.pluginPath + File.separator + "playerWaypointData.json";
    private static final String waypointDataPath = Main.pluginPath + File.separator + "waypointData.json";

    public static void createPlayerDataFile() {
        try {
            Main.console.info("No player data file was found. Creating default file now...");
            File file = new File(playerDataPath);
            if(!file.createNewFile()) return;
            FileWriter writer = new FileWriter(playerDataPath);
            writer.write("[]");
            writer.close();
            Main.console.info("Done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createWaypointDataFile() {
        try {
            Main.console.info("No waypoint data file was found. Creating default file now...");
            File file = new File(waypointDataPath);
            if(!file.createNewFile()) return;
            FileWriter writer = new FileWriter(waypointDataPath);
            writer.write("[]");
            writer.close();
            Main.console.info("Done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<waypoint> readStoredJSON(){
        try {
            File file = new File(waypointDataPath);

            if (!file.exists()) {
                waypointData.createWaypointDataFile();
            }

            String content;
            try {
                content = new Scanner(file).useDelimiter("\\Z").next();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                return null;
            }

            Gson gson = new Gson();
            Type waypointListType = new TypeToken<ArrayList<waypoint>>(){}.getType();

            return gson.fromJson(content, waypointListType);
        } catch (IOError | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Player_waypoint> readPlayerData() {
        try {
            File file = new File(playerDataPath);

            if (!file.exists()) {
                waypointData.createPlayerDataFile();
            }

            String content;
            try {
                content = new Scanner(file).useDelimiter("\\Z").next();
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                return null;
            }

            Gson gson = new Gson();
            Type waypointListType = new TypeToken<ArrayList<Player_waypoint>>(){}.getType();

            return gson.fromJson(content, waypointListType);
        } catch (IOError | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addNewWaypoint(waypoint waypoint) {
        List<waypoint> waypointList = Main.globalWaypoints;
        waypointList.add(waypoint);
        return true;
    }

    public static void addNewPlayerWaypoint(Player p,waypoint waypoint) {
        List<Player_waypoint> player_waypoints = Main.globalPlayerData;

        Player_waypoint pw = null;
        int index = 0;

        for(Player_waypoint gpw : player_waypoints) {
            if(gpw.uuid.equals(p.getUniqueId())){
                pw = gpw;
                break;
            }
            index++;
        }

        if(pw == null){
            Main.console.info("No player waypoint entry found. Creating entry.");
            pw = new Player_waypoint();
            pw.uuid = p.getUniqueId();
            pw.waypointList = new ArrayList<>();
            pw.waypointList.add(waypoint);
            player_waypoints.add(pw);
        } else {
            pw.waypointList.add(waypoint);
            player_waypoints.set(index, pw);
        }
        Main.globalPlayerData = player_waypoints;
    }

    public static int removeWaypoint(String name) {
        List<waypoint> waypoints = Main.globalWaypoints;
        List<Player_waypoint> pws = Main.globalPlayerData;

        //return 0 if waypoint DNE
        //return 1 if success
        //return 2 if internal error

        for( int i = 0; i < waypoints.size(); i++){
            if(waypoints.get(i).name.equalsIgnoreCase(name)) {
                waypoints.remove(i);

                //Remove waypoint from player data
                for(Player_waypoint pw: pws) {
                    for(int g = 0; g < pw.waypointList.size(); g++){
                        if(pw.waypointList.get(g).name.equalsIgnoreCase(name)){
                            pw.waypointList.remove(g);
                        }
                    }
                }





                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                //waypoint list type
                Type waypointListType = new TypeToken<ArrayList<waypoint>>(){}.getType();

                //player data list type

                Type playerDataType = new TypeToken<ArrayList<Player_waypoint>>(){}.getType();

                //waypoint list string
                String toWrite = gson.toJson(waypoints, waypointListType);

                //Player data string
                String pwToWrite = gson.toJson(pws, playerDataType);

                //Write Waypoints
                File file = new File(waypointDataPath);

                if(!(file.exists())) {
                    Main.console.info("Could not find waypointData.json!");
                    return 0;
                }

                try {
                    FileWriter writer = new FileWriter(waypointDataPath);
                    writer.write(toWrite);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return 2;
                }

                //Write Player Data;
                File pfile = new File(playerDataPath);

                if(!(pfile.exists())) {
                    Main.console.info("Could not find playerWaypointData.json!");
                    return 0;
                }

                try {
                    FileWriter writer = new FileWriter(playerDataPath);
                    writer.write(pwToWrite);
                    writer.close();

                    //Update global vars
                    Main.globalWaypoints = waypoints;
                    Main.globalPlayerData = pws;

                    return 1;
                } catch (IOException e) {
                    e.printStackTrace();
                    return 2;
                }
            }
        }
        return 0;
    }

    public static boolean exists(waypoint waypoint){
        List<waypoint> waypoints = Main.globalWaypoints;
        for (waypoint value : waypoints) {
            if (value.name.equalsIgnoreCase(waypoint.name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean saveDataToFiles() {
        //this method will get called periodically by the save methods to save the data that is in memory to their files.
        //having a separate method will allow us to save on the file write operations.

        //Announce intentions in Server console
        Main.console.info("Saving data...");

        //save Player waypoint data

        List<Player_waypoint> player_waypoints = Main.globalPlayerData;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type waypointListType = new TypeToken<ArrayList<Player_waypoint>>(){}.getType();
        String toWrite = gson.toJson(player_waypoints, waypointListType);

        File file = new File(playerDataPath);

        if(!(file.exists())) {
            waypointData.createWaypointDataFile();
        }

        try {
            FileWriter writer = new FileWriter(playerDataPath);
            writer.write(toWrite);
            writer.close();
            Main.console.info("Saved player waypoint data.");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //save waypoints
        List<waypoint> waypointList = Main.globalWaypoints;
        waypointListType = new TypeToken<ArrayList<waypoint>>(){}.getType();
        toWrite = gson.toJson(waypointList, waypointListType);

        file = new File(waypointDataPath);
        boolean exists = file.exists();

        if(!exists) {
            waypointData.createWaypointDataFile();
        }

        try {
            FileWriter writer = new FileWriter(waypointDataPath);
            writer.write(toWrite);
            writer.close();
            Main.console.info("Saved waypoint data.");
            Main.console.info("Done saving data :)");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
