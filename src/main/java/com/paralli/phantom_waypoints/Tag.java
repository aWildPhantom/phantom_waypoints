package com.paralli.phantom_waypoints;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class Tag {
    public static TextComponent tag() {
        TextComponent tag = new TextComponent("[");
        TextComponent Name = new TextComponent("Phantom Waypoints");
        Name.setColor(ChatColor.RED);
        tag.addExtra(Name);
        tag.addExtra("] ");
        return tag;
    }
}
