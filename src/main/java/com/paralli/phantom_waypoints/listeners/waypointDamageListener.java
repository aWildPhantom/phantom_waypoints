package com.paralli.phantom_waypoints.listeners;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class waypointDamageListener implements Listener {
    @EventHandler
    public static void onEntityDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof EnderCrystal){
            if(e.getEntity().getMetadata("pwaypoint").size() > 0){
                e.setCancelled(true);
            }
        }
    }
}
