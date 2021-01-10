package com.paralli.phantom_waypoints.functions;

import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.classes.Player_waypoint;
import com.paralli.phantom_waypoints.classes.Tag;
import com.paralli.phantom_waypoints.classes.waypoint;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class waypointFunctions {

    static void sendTeleportMenu(Player p){
        TextComponent message = new TextComponent();
        message.addExtra(Tag.tag());
        message.addExtra("Known Waypoints: ");
        message.addExtra("\n");

        List<Player_waypoint> playerWaypoints = Main.globalPlayerData;
        List<waypoint> waypoints = null;

        for(Player_waypoint pw: playerWaypoints){
            if(pw.uuid.equals(p.getUniqueId())){
                waypoints = pw.waypointList;
            }
        }

        if(waypoints == null){
            sendMessage(p, "You have yet to discover any waypoints!");
            return;
        }

        for (waypoint value : waypoints) {
            TextComponent comp = new TextComponent("[");
            TextComponent Link = new TextComponent(value.name);
            Link.setColor(ChatColor.GREEN);
            comp.addExtra(Link);
            comp.addExtra("]");
            comp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/waypoint teleport " + value.name + " CrudeVerificationKek69"));
            message.addExtra(comp);
            message.addExtra(" ");
        }

        p.spigot().sendMessage(message);
    }

    //send a general message to player with the appropriate tag element added to it
    public static void sendMessage(Player p, String m) {
        TextComponent message = new TextComponent("");
        message.addExtra(Tag.tag());
        message.addExtra(m);
        p.spigot().sendMessage(message);
    }

    public static void entryPoint(Player p, waypoint w) {
        if(!(hasDiscovered(p, w))){
            //send message
            sendMessage(p, "You discovered a new waypoint: "+w.name);

            //add to player discovered data
            waypointData.addNewPlayerWaypoint(p, w);
        }

        //display waypoint teleport list
        sendTeleportMenu(p);
    }

    static boolean hasDiscovered(Player p, waypoint w){
        List<waypoint> pwaypoints = null;
        List<Player_waypoint> playerList = Main.globalPlayerData;




        for(Player_waypoint pw: playerList){
            if(pw.uuid.equals(p.getUniqueId())){
                pwaypoints = pw.waypointList;
            }
        }

        //if the waypoint list couldn't be found, return false because we assume no waypoint whatsoever.
        if(pwaypoints == null){
            return false;
        }

        for(waypoint waypoint : pwaypoints){
            if(w == waypoint){
                return true;
            }
        }

        //default return
        return false;
    }
}
