package com.paralli.phantom_waypoints.functions;

import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.models.waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

import static com.paralli.phantom_waypoints.functions.waypointFunctions.entryPoint;

public class handleMoveEvent implements Listener {

    @EventHandler
    public void checkLocation(PlayerMoveEvent moveEvent) {
        Location playerLoc = moveEvent.getTo();

        List<waypoint> waypoints = Main.globalWaypoints;

        int fx = moveEvent.getFrom().getBlockX();
        int fy = moveEvent.getFrom().getBlockY();
        int fz = moveEvent.getFrom().getBlockZ();
        int tx = moveEvent.getTo().getBlockX();
        int ty = moveEvent.getTo().getBlockY();
        int tz = moveEvent.getTo().getBlockZ();

        //check if the player has moved at least one block. Will stop the repeat firing of the events.
        if(fx == tx && fy == ty && fz == tz){
            return;
        }


        for(waypoint w : waypoints){
            if(!(playerLoc.getWorld().equals(Bukkit.getServer().getWorld(w.world)))){
                continue;
            }

            Location loc =  new Location(Bukkit.getServer().getWorld(w.world), w.x, w.y, w.z);

            if (playerLoc.distance(loc) < 1) {
                if(moveEvent.getFrom().getBlockX()==loc.getBlockX() && moveEvent.getFrom().getBlockZ()==loc.getBlockZ()){
                    return;
                }
                entryPoint(moveEvent.getPlayer(), w);
            }
        }
    }
}
