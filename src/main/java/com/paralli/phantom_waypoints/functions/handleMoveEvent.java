package com.paralli.phantom_waypoints.functions;

import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.models.waypoint;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

        Block from = moveEvent.getFrom().getBlock();
        Block to = moveEvent.getTo().getBlock();

        int fx = from.getX();
        int fy = from.getY();
        int fz = from.getZ();
        int tx = to.getX();
        int ty = to.getY();
        int tz = to.getZ();

        //check if the player has moved at least one block. Will stop the repeat firing of the events.
        if(fx == tx && fy == ty && fz == tz){
            return;
        }


        for(waypoint w : waypoints){
            if(!(playerLoc.getWorld() == Bukkit.getServer().getWorld(w.world))){
                continue;
            }

            Location loc =  new Location(Bukkit.getServer().getWorld(w.world), w.x, w.y, w.z);

            if (playerLoc.distance(loc) < 0.5) {
                if(fx == loc.getBlockX() && fz == loc.getBlockZ()){
                    return;
                }
                entryPoint(moveEvent.getPlayer(), w);
            }
        }
    }
}
