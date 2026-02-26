package _1ms.FF;

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendSuggestionsEvent;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestions;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static _1ms.FF.CfgMgr.*;

public class EventMgr implements Listener {
    public static final Set<UUID> freezed = ConcurrentHashMap.newKeySet();

    private boolean isFrozen(HumanEntity p) {
        return freezed.contains(p.getUniqueId());
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        if (BLOCK_PLAYER_MOVE && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void PlayerSneakBlock(PlayerToggleSneakEvent e) {
        if (BLOCK_PLAYER_MOVE && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void PlayerSprintBlock(PlayerToggleSprintEvent e) {
        if (BLOCK_PLAYER_MOVE && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (BLOCK_PLAYER_INTERACT && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if (BLOCK_INTERACTING_WITH_ENTITY && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void blockAtEntityInteract(PlayerInteractAtEntityEvent e) {
        if (BLOCK_INTERACTING_WITH_ENTITY && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHit(PrePlayerAttackEntityEvent e) {
        if (BLOCK_PLAYER_HIT && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if (BLOCK_ITEM_DROP && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerAttemptPickupItemEvent e) {
        if (BLOCK_ITEM_PICKUP && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void PlayerCommand(PlayerCommandPreprocessEvent e) {
        var p = e.getPlayer();
        if (BLOCK_PLAYER_COMMANDS && isFrozen(p)) {
            if (!CMD_WHITELIST.isEmpty() &&
                    CMD_WHITELIST.contains(e.getMessage().split(" ")[0].substring(1).toLowerCase()))
                return;
            e.setCancelled(true);
            p.sendMessage(NO_COMMAND);
        }
    }
//When the player is frozen and cant enter cmds, also take away their tab completitions, experimental api->async.
    @EventHandler
    @SuppressWarnings("UnstableApiUsage")
    public void onAsyncTab(AsyncPlayerSendCommandsEvent<?> e) {
        if ((e.isAsynchronous() || !e.hasFiredAsync()) && BLOCK_PLAYER_COMMANDS && freezed.contains(e.getPlayer().getUniqueId())) {
            System.out.println("RM "+e.getCommandNode().getChildren());
            e.getCommandNode().getChildren().removeIf(c->!CMD_WHITELIST.contains(c.getName().toLowerCase()));
        }
    }

    @EventHandler
    public void PlayerInvC(InventoryClickEvent e) {
        if (BLOCK_INVENTORY_CLICK && isFrozen(e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (BLOCK_GUI_OPEN && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void PlayerHit(EntityDamageEvent e) {
        if (BLOCK_PLAYER_DAMAGE && e.getEntity() instanceof Player p && isFrozen(p)) {
            e.setCancelled(true);
            if (e instanceof EntityDamageByEntityEvent damageEvent &&
                    damageEvent.getDamager() instanceof Player p0)
                p0.sendMessage(CANT_HIT_FROZEN_PLAYER);
        }
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent e) {
        if (BLOCK_ITEM_SWAP && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void HandleLeave(PlayerQuitEvent e) {
        var p = e.getPlayer();
        if (freezed.remove(p.getUniqueId())) {
            Freezer.coreUnfreeze(p,null);
            if (!CMD_ON_LEAVE.isEmpty()) {
                var cmds = CMD_ON_LEAVE.split(";");
                for (String cmd : cmds) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                            Objects.requireNonNull(cmd).replace("<player>", p.getName()));
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (BLOCK_TELEPORT && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        var p = e.getPlayer();
        if (BLOCK_CHAT && isFrozen(p)) {
            e.setCancelled(true);
            p.sendMessage(NO_CHAT);
        }
    }

}
