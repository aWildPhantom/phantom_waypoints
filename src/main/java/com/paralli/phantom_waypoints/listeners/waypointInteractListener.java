package com.paralli.phantom_waypoints.listeners;

import com.paralli.phantom_waypoints.Main;
import com.paralli.phantom_waypoints.models.wayplayer;
import com.paralli.phantom_waypoints.models.waypoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class waypointInteractListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEntityEvent e){
        if(!(e.getHand().equals(EquipmentSlot.OFF_HAND))){
            return;
        }

        if(!(e.getRightClicked().getType().equals(EntityType.ENDER_CRYSTAL))){
            return;
        }

        if(e.getRightClicked().getMetadata("pwaypoint").size() > 0){
            Player p = e.getPlayer();
            p.sendMessage("[DEBUG] You right-clicked a waypoint!");

            if(!(p.hasPermission("pwaypoint.use"))){
                p.sendMessage("Sorry, You don't have permission to use the waypoints!");
                return;
            }

            //get waypoint from waypoints list for ease of
            waypoints w = null;
            for(int x = 0; x < Main.waypoint_list.size(); x++){
                p.sendMessage("[DEBUG] searching: "+Main.waypoint_list.get(x).UUID.toString());
                if(Main.waypoint_list.get(x).UUID.equals(e.getRightClicked().getUniqueId())) {
                    p.sendMessage("[DEBUG] Found the waypoint in the list!");
                    w = Main.waypoint_list.get(x);
                    break;
                }
            }

            //check if this waypoint exists in the player's discovered list
            for(int i = 0; i < Main.wayplayer_list.size(); i++){
                if(Main.wayplayer_list.get(i).playerUUID.equals(p.getUniqueId())){
                    wayplayer found = Main.wayplayer_list.get(i);
                    //now check if the waypoint exists in the player's known list
                    for(int y = 0; y < found.known.size(); y++){
                        p.sendMessage("[DEBUG] searching known: "+found.known.get(y).UUID.toString() );
                        if(found.known.get(y).UUID.equals(e.getRightClicked().getUniqueId())){
                            openInventory(p, found, e.getRightClicked());
                            return;
                        }
                    }

                    //Assume it was not found, thus we need to add the waypoint to the player's list
                    if(w!=null){found.known.add(w); p.sendMessage("[DEBUG] added to known");}else{p.sendMessage("Something went wrong while discovering this waypoint!"); return;}
                    p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] Congrats! You discovered: "+e.getRightClicked().getName());
                    p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] You may now travel to it from any other waypoint!");
                    openInventory(p, found, e.getRightClicked());
                    return;

                }
            }

            p.sendMessage("[DEBUG] You weren't found in our player list. Fixing that now.");
            wayplayer pn = new wayplayer();
            pn.playerUUID = p.getUniqueId();
            pn.known = new ArrayList<waypoints>();
            if(w!=null){pn.known.add(w); p.sendMessage("[DEBUG] added");}
            p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] Congrats! You discovered: "+e.getRightClicked().getName());
            p.sendMessage(ChatColor.WHITE+"["+ChatColor.AQUA+"PW"+ChatColor.WHITE+"] You may now travel to it from any other waypoint!");
            Main.wayplayer_list.add(pn);
            openInventory(p, pn, e.getRightClicked());
            return;
        }
    }


    private void openInventory(Player p, wayplayer wp, Entity ent){
        Inventory inventory = Bukkit.createInventory(null, 9, ent.getName());


        for(int i = 0; i < wp.known.size(); i++){
            String name = wp.known.get(i).name;
            inventory.addItem(makeGuiItem(Material.END_CRYSTAL,name));
        }


        p.openInventory(inventory);
    }

    protected ItemStack makeGuiItem(final Material mat, final String name){
        ItemStack made = new ItemStack(mat);
        final ItemMeta meta = made.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        made.setItemMeta(meta);
        return made;
    }
}
