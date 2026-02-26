package _1ms.FF;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
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
        cmdtarget.updateCommands();
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
        cmdtarget.getWorld().playSound(cmdtarget, Sound.ENTITY_PLAYER_HURT_FREEZE, 1,1);
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
            freezed.remove(target.getUniqueId());
            coreUnfreeze(target, sender);
            sender.sendMessage(PLAYER_UNFROZEN);
        } else {
            if (!shDown && freezed.isEmpty()) {
                sender.sendMessage(NO_PLAYER_FROZEN);
                return;
            }
            var iterator = freezed.iterator();
            while (iterator.hasNext()) {
                var pUUID = iterator.next();
                iterator.remove();
                var player = Objects.requireNonNull(Bukkit.getPlayer(pUUID));
                coreUnfreeze(player, sender);
            }
            if (!shDown)
                sender.sendMessage(ALL_PLAYERS_UNFROZEN);

        }
        if (!shDown)
            notify(true, all, target, sender);
    }

    public static void coreUnfreeze(Player cmdtarget, CommandSender sender) {
        cmdtarget.setAllowFlight(false);
        cmdtarget.updateCommands();
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
                p.sendMessage(Main.mm.deserialize((un ? STAFF_NOTICE_UN : STAFF_NOTICE),
                        Placeholder.component("target", cmdtarget.displayName()), getHolder(sender)));
                return;
            }
            p.sendMessage(Main.mm.deserialize((un? STAFF_NOTICE_ALL_UN : STAFF_NOTICE_ALL), getHolder(sender)));
        });
    }

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
        return Placeholder.component("sender", sender instanceof Player p?p.displayName():Component.text(sender.getName()));
    }

}
