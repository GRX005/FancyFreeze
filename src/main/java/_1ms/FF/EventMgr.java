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

import com.destroystokyo.paper.event.brigadier.AsyncPlayerSendCommandsEvent;
import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static _1ms.FF.CfgMgr.*;

public class EventMgr implements Listener {
    public static final Set<UUID> freezed = ConcurrentHashMap.newKeySet();

    private boolean isFrozen(HumanEntity p) {
        return freezed.contains(p.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerMoveEvent(PlayerMoveEvent e) {
        if (BLOCK_PLAYER_MOVE && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerSneakBlock(PlayerToggleSneakEvent e) {
        if (BLOCK_PLAYER_MOVE && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerSprintBlock(PlayerToggleSprintEvent e) {
        if (BLOCK_PLAYER_MOVE && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (BLOCK_PLAYER_INTERACT && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        if (BLOCK_INTERACTING_WITH_ENTITY && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void blockAtEntityInteract(PlayerInteractAtEntityEvent e) {
        if (BLOCK_INTERACTING_WITH_ENTITY && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerHit(PrePlayerAttackEntityEvent e) {
        if (BLOCK_PLAYER_HIT && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemDrop(PlayerDropItemEvent e) {
        if (BLOCK_ITEM_DROP && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemPickup(PlayerAttemptPickupItemEvent e) {
        if (BLOCK_ITEM_PICKUP && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    @SuppressWarnings("UnstableApiUsage")
    public void onAsyncTab(AsyncPlayerSendCommandsEvent<?> e) {
        if ((e.isAsynchronous() || !e.hasFiredAsync()) && BLOCK_PLAYER_COMMANDS && freezed.contains(e.getPlayer().getUniqueId()))
            e.getCommandNode().getChildren().removeIf(c->!CMD_WHITELIST.contains(c.getName().toLowerCase()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerInvC(InventoryClickEvent e) {
        if (BLOCK_INVENTORY_CHANGES && isFrozen(e.getWhoClicked()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent e) {
        if (BLOCK_GUI_OPEN && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void PlayerHit(EntityDamageEvent e) {
        if (BLOCK_PLAYER_DAMAGE && e.getEntity() instanceof Player p && isFrozen(p)) {
            e.setCancelled(true);
            if (e instanceof EntityDamageByEntityEvent damageEvent &&
                    damageEvent.getDamager() instanceof Player p0)
                p0.sendMessage(CANT_HIT_FROZEN_PLAYER);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent e) {
        if (BLOCK_TELEPORT && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onChat(AsyncChatEvent e) {
        var p = e.getPlayer();
        if (BLOCK_CHAT && isFrozen(p)) {
            e.setCancelled(true);
            p.sendMessage(NO_CHAT);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBookEdit(PlayerEditBookEvent e) {
        if (BLOCK_INVENTORY_CHANGES && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void xpPickup(PlayerPickupExperienceEvent e) {
        if (BLOCK_ITEM_PICKUP && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void SlotChangeBlock(PlayerItemHeldEvent e) {
        if (BLOCK_INVENTORY_CHANGES && isFrozen(e.getPlayer()))
            e.setCancelled(true);
    }


}
