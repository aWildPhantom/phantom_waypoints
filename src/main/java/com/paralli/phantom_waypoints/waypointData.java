package com.paralli.phantom_waypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class waypointData {
    public static void createDataFile() {
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
            boolean exists = file.exists();

            if (exists == false) {
                waypointData.createDataFile();
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

    public static boolean addNew(waypoint waypoint) {
        List<waypoint> waypointList = readStoredJSON();
        waypointList.add(waypoint);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type waypointListType = new TypeToken<ArrayList<waypoint>>(){}.getType();
        String toWrite = gson.toJson(waypointList, waypointListType);

        File file = new File(Main.pluginPath + File.separator + "waypointData.json");
        boolean exists = file.exists();

        if(exists == false) {
            waypointData.createDataFile();
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

    public static int removeWaypoint(String name) {
        List<waypoint> waypoints = readStoredJSON();

        //return 0 if waypoint DNE
        //return 1 if success
        //return 2 if internal error

        for( int i = 0; i < waypoints.size(); i++){
            if(waypoints.get(i).name.equalsIgnoreCase(name)) {
                waypoints.remove(i);
                Gson gson = new Gson();
                Type waypointListType = new TypeToken<ArrayList<waypoint>>(){}.getType();
                String toWrite = gson.toJson(waypoints, waypointListType);

                File file = new File(Main.pluginPath + File.separator + "waypointData.json");
                boolean exists = file.exists();

                if(exists == false) {
                    return 0;
                }

                try {
                    FileWriter writer = new FileWriter(Main.pluginPath + File.separator + "waypointData.json");
                    writer.write(toWrite);
                    writer.close();
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
        List<waypoint> waypoints = readStoredJSON();
        for( int i = 0; i < waypoints.size(); i++){
            if(waypoints.get(i).name.equalsIgnoreCase(waypoint.name)){
                return true;
            }
        }
        return false;
    }
}
