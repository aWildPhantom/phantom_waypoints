package com.paralli.phantom_waypoints.models;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class wayplayer implements Serializable {
    public UUID playerUUID;
    public List<waypoints> known;
}
