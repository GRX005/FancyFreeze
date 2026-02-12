package _1ms.FF;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class CfgMgr {//TODO CONVERT TO automatic assignments
    public static Component prefix;
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
    public static boolean BLOCK_PLAYER_PLACE;
    public static boolean BLOCK_PLAYER_BREAK;
    public static boolean BLOCK_PLAYER_INTERACT;
    public static boolean BLOCK_PLAYER_HIT;
    public static boolean BLOCK_ITEM_DROP;
    public static boolean BLOCK_ITEM_PICKUP;
    public static boolean BLOCK_PLAYER_COMMANDS;
    public static boolean BLOCK_INTERACTING_WITH_ENTITY;
    public static boolean BLOCK_INVENTORY_CLICK;
    public static boolean BLOCK_PLAYER_DAMAGE;
    public static boolean BLOCK_ITEM_SWAP;
    public static boolean BLOCK_GUI_OPEN;
    public static boolean BLOCK_TELEPORT;
    public static boolean BLOCK_CHAT;

    public static List<String> CMD_WHITELIST = new ArrayList<>();
    public static String CMD_ON_LEAVE;

    public static void load() {
        cfg = Main.pl.getConfig();
//Messages, dynamic ones need to be read as String
        prefix = getMessage("Prefix");
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

        FROZEN_BY = getStr("FROZEN_BY");
        STAFF_NOTICE = getStr("STAFF_NOTICE");
        STAFF_NOTICE_ALL = getStr("STAFF_NOTICE_ALL");
        UNFROZEN_BY = getStr("UNFROZEN_BY");
        STAFF_NOTICE_UN = getStr("STAFF_NOTICE_UN");
        STAFF_NOTICE_ALL_UN = getStr("STAFF_NOTICE_ALL_UN");

//Extra visuals
        SPAWN_PARTICLE = getFeature("SPAWN_PARTICLES");
        SPAWN_EFFECT = getFeature("FREEZE_EFFECT");
        GLOWING = getFeature("GLOWING_EFFECT");
        ACTION_BAR_MSG = getFeature("ACTION_BAR_MSG");

        BLOCK_PLAYER_MOVE = getFeature("BLOCK_PLAYER_MOVE");
        BLOCK_PLAYER_PLACE = getFeature("BLOCK_PLAYER_PLACE");
        BLOCK_PLAYER_BREAK = getFeature("BLOCK_PLAYER_BREAK");
        BLOCK_PLAYER_INTERACT = getFeature("BLOCK_PLAYER_INTERACT");
        BLOCK_PLAYER_HIT = getFeature("BLOCK_PLAYER_HIT");
        BLOCK_ITEM_DROP = getFeature("BLOCK_ITEM_DROP");
        BLOCK_ITEM_PICKUP = getFeature("BLOCK_ITEM_PICKUP");
        BLOCK_PLAYER_COMMANDS = getFeature("BLOCK_PLAYER_COMMANDS");
        BLOCK_INTERACTING_WITH_ENTITY = getFeature("BLOCK_INTERACTING_WITH_ENTITY");
        BLOCK_INVENTORY_CLICK = getFeature("BLOCK_INVENTORY_CLICK");
        BLOCK_PLAYER_DAMAGE = getFeature("BLOCK_PLAYER_DAMAGE");
        BLOCK_ITEM_SWAP = getFeature("BLOCK_ITEM_SWAP");
        BLOCK_GUI_OPEN = getFeature("BLOCK_GUI_OPEN");
        BLOCK_TELEPORT = getFeature("BLOCK_TELEPORT");
        BLOCK_CHAT = getFeature("BLOCK_CHAT");
//Utils
        final String fp = "SkyFreeze_Config.Features.";
        CMD_WHITELIST = cfg.getStringList(fp + "BLOCKED_COMMANDS_WHITELIST");
        CMD_ON_LEAVE = cfg.getString(fp + "COMMAND_ON_LEAVE");
    }
    
    private static FileConfiguration cfg;
    private static final MiniMessage mm = MiniMessage.miniMessage();
    
    private static Component getMessage(String path) {
        var msg = mm.deserialize(getStr(path));
        if (!path.contains("Prefix") && !path.contains("ACTION_BAR"))//Apply the prefixes to the chat msgs only.
            msg=prefix.append(msg);
        return msg;
    }
    //Separate as we store the replaceable ones as Strings, so this is used to read that directly, and we store the static ones as Component from the start
    private static String getStr(String path) {
        return cfg.getString("SkyFreeze_Config.Messages." + path);
    }
    
    private static boolean getFeature(String path) {
        return cfg.getBoolean("SkyFreeze_Config.Features."+path);
    }
}
