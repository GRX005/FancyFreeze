package _1ms.FF;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Objects;

import static _1ms.FF.CfgMgr.*;
import static _1ms.FF.EventMgr.freezed;

public class Freezer {

    public static void freeze(CommandSender sender, Player target) {
        var all = target==null;
        if (!all) {
            if (sender.getName().equals(target.getName())) {
                sender.sendMessage(NO_SELF_FREEZE);
                return;
            }
            if (target.hasPermission(Perm.Bypass.s)) {
                sender.sendMessage(PLAYER_HAS_PERM);
                return;
            }
            if (freezed.contains(target.getUniqueId())) {
                sender.sendMessage(PLAYER_ALREADY_FROZEN);
                return;
            }
            coreFreeze(target, sender);
            sender.sendMessage(PLAYER_FROZEN);
        } else {
            //op check
            var fP = new ArrayList<Player>(Bukkit.getOnlinePlayers().stream()
                    .filter(p -> sender!=p && !p.hasPermission(Perm.Bypass.s) && !freezed.contains(p.getUniqueId()))
                    .toList());
            if (fP.isEmpty()) {
                sender.sendMessage(NO_FREEZABLE);
                return;
            }
            //Collects all players beforehand to be able to send a message if there are no players that can be frozen.
            for (Player p : fP) {
                coreFreeze(p, sender);
            }
            sender.sendMessage(ALL_PLAYERS_FROZEN);
        }
        //send to all players
        notify(false, all, target, sender);
    }

    private static void coreFreeze(Player cmdtarget, CommandSender sender) {
        freezed.add(cmdtarget.getUniqueId());
        cmdtarget.setAllowFlight(true);
        if(SPAWN_EFFECT)
            cmdtarget.setFreezeTicks(Integer.MAX_VALUE);
        if(GLOWING)
            cmdtarget.setGlowing(true);
//        if(SPAWN_PARTICLE)
//            handleParticle(cmdtarget);
        if (ACTION_BAR_MSG)
            handleActionBar(cmdtarget);
        //cmdtarget.sendMessage(FROZEN_BY.replace("%player%", sender.getName()));
        cmdtarget.sendMessage(Main.mm.deserialize(FROZEN_BY, getHolder(sender)));
    }

    public static void unFreeze(CommandSender sender, Player target) {
        var all = target == null;
        var shDown = sender == null;
        if(!all) {
            assert sender!=null;
            if (!freezed.contains(target.getUniqueId())) {
                sender.sendMessage(PLAYER_NOT_FROZEN);
                return;
            }
            coreUnfreeze(target, sender);
            freezed.remove(target.getUniqueId());
            sender.sendMessage(PLAYER_UNFROZEN);
        } else {
            if (!shDown && freezed.isEmpty()) {
                sender.sendMessage(NO_PLAYER_FROZEN);
                return;
            }
            var iterator = freezed.iterator();
            while (iterator.hasNext()) {
                var pUUID = iterator.next();
                var player = Objects.requireNonNull(Bukkit.getPlayer(pUUID));
                coreUnfreeze(player, sender);
                iterator.remove();
            }
            if (!shDown)
                sender.sendMessage(ALL_PLAYERS_UNFROZEN);

        }
        if (!shDown)
            notify(true, all, target, sender);
    }

    public static void coreUnfreeze(Player cmdtarget, CommandSender sender) {
        cmdtarget.setAllowFlight(false);
        if(SPAWN_EFFECT)
            cmdtarget.setFreezeTicks(0);
        if(GLOWING)
            cmdtarget.setGlowing(false);
        if (sender!=null)
            cmdtarget.sendMessage(Main.mm.deserialize(UNFROZEN_BY, getHolder(sender)));
    }

    private static void notify(boolean un, boolean all, Player cmdtarget, CommandSender sender) {
        Bukkit.getOnlinePlayers().stream().filter(p->p.hasPermission(Perm.Notify.s)&&p!=sender).forEach(p-> {
            if (!all) {
                //p.sendMessage((un ? STAFF_NOTICE_UN : STAFF_NOTICE).replace("%target%", cmdtarget.getName()).replace("%sender%", sender.getName()));
                p.sendMessage(Main.mm.deserialize((un ? STAFF_NOTICE_UN : STAFF_NOTICE), Placeholder.component("target", cmdtarget.displayName()), getHolder(sender)));
                return;
            }
            p.sendMessage(Main.mm.deserialize((un? STAFF_NOTICE_ALL_UN : STAFF_NOTICE_ALL), getHolder(sender)));
        });
    }

//    private static void handleParticle(Player cmdtarget) {
//        // 1. Pre-computation (Do this once, before the task starts)
//        final int points = 16;
//        final double radius = 1.5;
//        final double[] xOffsets = new double[points];
//        final double[] zOffsets = new double[points];
//
//// Pre-calculate offsets to avoid Math.sin/cos every tick
//        for (int i = 0; i < points; i++) {
//            double angle = 2 * Math.PI * i / points;
//            xOffsets[i] = radius * Math.cos(angle);
//            zOffsets[i] = radius * Math.sin(angle);
//        }
//
//        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.WHITE, 1);
//        final int[] step = {0}; // Use an integer array wrapper for the mutable index
//
//        Bukkit.getScheduler().runTaskTimer(Main.pl, t -> {
//            // 2. Validity Check
//            if (!freezed.contains(cmdtarget.getUniqueId())) {
//                t.cancel();
//                return;
//            }
//
//            // 3. Fast Lookup (No trigonometry here)
//            int i = step[0];
//            double x = cmdtarget.getX() + xOffsets[i];
//            double y = cmdtarget.getY() + 2;
//            double z = cmdtarget.getZ() + zOffsets[i];
//
//            // Spawn particle
//            cmdtarget.getWorld().spawnParticle(Particle.DUST, x, y, z, 1, 0, 0, 0, 1, dustOptions);
//
//            // 4. Update Index
//            if (++step[0] >= points) {
//                step[0] = 0;
//            }
//        }, 0, 1);
//    }
//TODO maybe VT?
    private static void handleActionBar(Player target) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.pl, t-> {
            if (!freezed.contains(target.getUniqueId())) {
                t.cancel();
                return;
            }
            target.sendActionBar(FROZEN_ACTION_BAR);
        },0L,40L);

    }

    private static TagResolver.Single getHolder(CommandSender sender) {
        return Placeholder.component("player", sender instanceof Player p?p.displayName():Component.text(sender.getName()));
    }

}
