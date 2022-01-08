package com.paralli.phantom_waypoints.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class Player_waypoint implements Serializable {

    public UUID uuid;

    public List<waypoint> waypointList;
}
