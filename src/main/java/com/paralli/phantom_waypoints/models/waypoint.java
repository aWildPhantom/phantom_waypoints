package com.paralli.phantom_waypoints.models;

import java.io.Serializable;

public class waypoint implements Serializable {

    //Name of waypoint
    public String name;

    //Which world the waypoint exists in
    public String world;

    //Player Positioning
    public float pitch;
    public float yaw;
    public Double x;
    public int y;
    public Double z;
}
