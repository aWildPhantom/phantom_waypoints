package com.paralli.phantom_waypoints.models;

import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.checkerframework.checker.units.qual.C;

import java.io.Serializable;
import java.util.UUID;

public class waypoints implements Serializable {

    //data for waypoint identification
    public UUID UUID;
    public String name;


    //world location data
    public int xcoord;
    public int ycoord;
    public int zcoord;
    public String worldName;

    //data for where to teleport player
    public int offsetX;
    public int offsetZ;
    public BlockFace facing;


    public transient Chunk chunk;
}
