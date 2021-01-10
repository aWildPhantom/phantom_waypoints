package com.paralli.phantom_waypoints.functions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.classes.Player_waypoint;
import com.paralli.phantom_waypoints.classes.waypoint;
import org.bukkit.entity.Player;


import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class waypointData {

    public static void createPlayerDataFile() {
        try {
            Main.console.info("No player data file was found. Creating default file now...");
            File file = new File(Main.pluginPath + File.separator + "playerWaypointData.json");
            file.createNewFile();
            FileWriter writer = new FileWriter(Main.pluginPath + File.separator + "playerWaypointData.json");
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
            File file = new File(Main.pluginPath + File.separator + "waypointData.json");
            file.createNewFile();
            FileWriter writer = new FileWriter(Main.pluginPath + File.separator + "waypointData.json");
            writer.write("[]");
            writer.close();
            Main.console.info("Done.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<waypoint> readStoredJSON(){
        try {
            File file = new File(Main.pluginPath + File.separator + "waypointData.json");

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
            List<waypoint> waypointList = gson.fromJson(content, waypointListType);

            return waypointList;
        } catch (IOError | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Player_waypoint> readPlayerData() {
        try {
            File file = new File(Main.pluginPath + File.separator + "playerWaypointData.json");

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
            List<Player_waypoint> playerWaypointList = gson.fromJson(content, waypointListType);

            return playerWaypointList;
        } catch (IOError | IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addNewWaypoint(waypoint waypoint) {
        List<waypoint> waypointList = Main.globalWaypoints;
        waypointList.add(waypoint);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type waypointListType = new TypeToken<ArrayList<waypoint>>(){}.getType();
        String toWrite = gson.toJson(waypointList, waypointListType);

        File file = new File(Main.pluginPath + File.separator + "waypointData.json");
        boolean exists = file.exists();

        if(exists == false) {
            waypointData.createWaypointDataFile();
        }

        try {
            FileWriter writer = new FileWriter(Main.pluginPath + File.separator + "waypointData.json");
            writer.write(toWrite);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addNewPlayerWaypoint(Player p,waypoint waypoint) {
        List<Player_waypoint> player_waypoints = Main.globalPlayerData;

        Player_waypoint pw = null;
        int index = 0;

        for(Player_waypoint gpw : player_waypoints) {
            if(gpw.uuid.equals(p.getUniqueId())){
                Main.console.info("Found an entry for UUID: "+String.valueOf(p.getUniqueId()));
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

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type waypointListType = new TypeToken<ArrayList<Player_waypoint>>(){}.getType();
        String toWrite = gson.toJson(player_waypoints, waypointListType);

        File file = new File(Main.pluginPath + File.separator + "playerWaypointData.json");

        if(!(file.exists())) {
            waypointData.createWaypointDataFile();
        }

        try {
            FileWriter writer = new FileWriter(Main.pluginPath + File.separator + "playerWaypointData.json");
            writer.write(toWrite);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
                File file = new File(Main.pluginPath + File.separator + "waypointData.json");

                if(!(file.exists())) {
                    Main.console.info("Could not find waypointData.json!");
                    return 0;
                }

                try {
                    FileWriter writer = new FileWriter(Main.pluginPath + File.separator + "playerWaypointData.json");
                    writer.write(toWrite);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return 2;
                }

                //Write Player Data;
                File pfile = new File(Main.pluginPath + File.separator + "playerWaypointData.json");

                if(!(pfile.exists())) {
                    Main.console.info("Could not find playerWaypointData.json!");
                    return 0;
                }

                try {
                    FileWriter writer = new FileWriter(Main.pluginPath + File.separator + "playerWaypointData.json");
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
}
