/*
    This file is part of the FancyFreeze project, licensed under the
    GNU General Public License v3.0

    Copyright (C) 2024-2026 _1ms (GRX005)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package _1ms.FF;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CfgMgr {//TODO CONVERT TO automatic assignments?
    public static String prefix;
    public static Component RELOAD;
    public static Component NO_SELF_FREEZE;
    public static Component PLAYER_HAS_PERM;
    public static Component PLAYER_ALREADY_FROZEN;
    public static Component PLAYER_FROZEN;
    public static Component PLAYER_UNFROZEN;
    public static String FROZEN_BY;
    public static String UNFROZEN_BY;
    public static Component NO_FREEZABLE;
    public static Component ALL_PLAYERS_FROZEN;
    public static Component ALL_PLAYERS_UNFROZEN;
    public static String  STAFF_NOTICE;
    public static String STAFF_NOTICE_ALL;
    public static String  STAFF_NOTICE_UN;
    public static String STAFF_NOTICE_ALL_UN;
    public static Component PLAYER_NOT_FROZEN;
    public static Component NO_PLAYER_FROZEN;
    public static Component FROZEN_ACTION_BAR;
    public static Component CANT_HIT_FROZEN_PLAYER;
    public static Component NO_COMMAND;
    public static Component NO_CHAT;

    public static boolean SPAWN_PARTICLE;
    public static boolean SPAWN_EFFECT;
    public static boolean GLOWING;
    public static boolean ACTION_BAR_MSG;

    public static boolean BLOCK_PLAYER_MOVE;
    public static boolean BLOCK_PLAYER_INTERACT;
    public static boolean BLOCK_PLAYER_HIT;
    public static boolean BLOCK_ITEM_DROP;
    public static boolean BLOCK_ITEM_PICKUP;
    public static boolean BLOCK_PLAYER_COMMANDS;
    public static boolean BLOCK_INTERACTING_WITH_ENTITY;
    public static boolean BLOCK_INVENTORY_CHANGES;
    public static boolean BLOCK_PLAYER_DAMAGE;
    public static boolean BLOCK_ITEM_SWAP;
    public static boolean BLOCK_GUI_OPEN;
    public static boolean BLOCK_TELEPORT;
    public static boolean BLOCK_CHAT;
    public static boolean KICK_FROM_VEHICLE;

    public static List<String> CMD_WHITELIST = new ArrayList<>();
    public static String CMD_ON_LEAVE;

    public static void load() {
        cfg = Main.pl.getConfig();
//Messages, dynamic ones need to be read as String
        prefix = cfg.getString("FancyFreeze_Config.Messages.Prefix");
        NO_SELF_FREEZE = getMessage("NO_SELF_FREEZE");
        PLAYER_HAS_PERM = getMessage("PLAYER_HAS_PERM");
        RELOAD = getMessage("RELOAD");
        PLAYER_ALREADY_FROZEN = getMessage("PLAYER_ALREADY_FROZEN");
        PLAYER_FROZEN = getMessage("PLAYER_FROZEN");
        NO_FREEZABLE = getMessage("NO_FREEZABLE");
        ALL_PLAYERS_FROZEN = getMessage("ALL_PLAYERS_FROZEN");
        PLAYER_NOT_FROZEN = getMessage("PLAYER_NOT_FROZEN");
        PLAYER_UNFROZEN = getMessage("PLAYER_UNFROZEN");
        NO_PLAYER_FROZEN = getMessage("NO_PLAYER_FROZEN");
        ALL_PLAYERS_UNFROZEN = getMessage("ALL_PLAYERS_UNFROZEN");
        FROZEN_ACTION_BAR = getMessage("FROZEN_ACTION_BAR");
        CANT_HIT_FROZEN_PLAYER = getMessage("CANT_HIT_FROZEN_PLAYER");
        NO_COMMAND = getMessage("NO_COMMAND");
        NO_CHAT = getMessage("NO_CHAT");

        FROZEN_BY = getStrP("FROZEN_BY");
        STAFF_NOTICE = getStrP("STAFF_NOTICE");
        STAFF_NOTICE_ALL = getStrP("STAFF_NOTICE_ALL");
        UNFROZEN_BY = getStrP("UNFROZEN_BY");
        STAFF_NOTICE_UN = getStrP("STAFF_NOTICE_UN");
        STAFF_NOTICE_ALL_UN = getStrP("STAFF_NOTICE_ALL_UN");

//Extra visuals
        SPAWN_PARTICLE = getFeature("SPAWN_PARTICLES");
        SPAWN_EFFECT = getFeature("FREEZE_EFFECT");
        GLOWING = getFeature("GLOWING_EFFECT");
        ACTION_BAR_MSG = getFeature("ACTION_BAR_MSG");

        BLOCK_PLAYER_MOVE = getFeature("BLOCK_PLAYER_MOVE");
        BLOCK_PLAYER_INTERACT = getFeature("BLOCK_PLAYER_INTERACT");
        BLOCK_PLAYER_HIT = getFeature("BLOCK_PLAYER_HIT");
        BLOCK_ITEM_DROP = getFeature("BLOCK_ITEM_DROP");
        BLOCK_ITEM_PICKUP = getFeature("BLOCK_ITEM_PICKUP");
        BLOCK_PLAYER_COMMANDS = getFeature("BLOCK_PLAYER_COMMANDS");
        BLOCK_INTERACTING_WITH_ENTITY = getFeature("BLOCK_INTERACTING_WITH_ENTITY");
        BLOCK_INVENTORY_CHANGES = getFeature("BLOCK_INVENTORY_CHANGES");
        BLOCK_PLAYER_DAMAGE = getFeature("BLOCK_PLAYER_DAMAGE");
        BLOCK_ITEM_SWAP = getFeature("BLOCK_ITEM_SWAP");
        BLOCK_GUI_OPEN = getFeature("BLOCK_GUI_OPEN");
        BLOCK_TELEPORT = getFeature("BLOCK_TELEPORT");
        BLOCK_CHAT = getFeature("BLOCK_CHAT");
        KICK_FROM_VEHICLE = getFeature("KICK_FROM_VEHICLE");
//Utils
        final String fp = "FancyFreeze_Config.Features.";
        CMD_WHITELIST = Arrays.stream(Objects.requireNonNull(cfg.getString(fp + "BLOCKED_COMMANDS_WHITELIST")).split(";")).toList();
        CMD_ON_LEAVE = cfg.getString(fp + "COMMAND_ON_LEAVE");
    }
    
    private static FileConfiguration cfg;
    private static final MiniMessage mm = MiniMessage.miniMessage();
    
    private static Component getMessage(String path) {
        return mm.deserialize(getStrP(path));
    }
    //Separate as we store the replaceable ones as Strings, so this is used to read that directly, and we store the static ones as Component from the start
    private static String getStrP(String path) {
        return (path.contains("ACTION_BAR")? "": prefix)+cfg.getString("FancyFreeze_Config.Messages." + path);
    }
    
    private static boolean getFeature(String path) {
        return cfg.getBoolean("FancyFreeze_Config.Features."+path);
    }
}
