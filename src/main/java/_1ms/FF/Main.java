package _1ms.FF;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

import static _1ms.FF.EventMgr.freezed;

public final class Main extends JavaPlugin {

    public static Main pl;
    public static MiniMessage mm;

    @Override
    public void onEnable() {
        pl=this;
        mm=MiniMessage.miniMessage();
        // Plugin startup logic
        saveDefaultConfig();
        CfgMgr.load();
        getServer().getPluginManager().registerEvents(new EventMgr(), this);
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, cmds-> {
            cmds.registrar().register(CmdMgr.freezeCmd());
            cmds.registrar().register(CmdMgr.freezeAllCmd());
            cmds.registrar().register(CmdMgr.unFreezeCmd());
            cmds.registrar().register(CmdMgr.unFreezeAllCmd());
            cmds.registrar().register(CmdMgr.reloadCmd());

        });
        if (CfgMgr.SPAWN_PARTICLE)
            handleParticle();
        getLogger().info("FancyFreeze has been loaded.");
    }

    @Override
    public void onDisable() {
        Freezer.unFreeze(null,null);
        getLogger().info("FancyFreeze has been unloaded.");
    }
    //TODO OPTIMISE
    private void handleParticle() {
        final double radius = 1.5;
        final int points = 16;
        final double[] xOff = new double[points], zOff = new double[points];

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            xOff[i] = radius * Math.cos(angle);
            zOff[i] = radius * Math.sin(angle);
        }

        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.WHITE, 1);
        final int[] tick = {0}; // Use an integer array wrapper for the mutable index

        Bukkit.getScheduler().runTaskTimer(Main.pl, t ->{
            int i = tick[0];
            tick[0] = (i + 1) % points;
            freezed.forEach(p->{
                var target = Objects.requireNonNull(Bukkit.getPlayer(p));

                var x = target.getX() + xOff[i];
                var y = target.getY() + 2;
                var z = target.getZ() + zOff[i];

                // Spawn particle
                target.getWorld().spawnParticle(Particle.DUST, x,y,z, 1, 0, 0, 0, 1, dustOptions);
            });
        }, 0, 1);
    }

}
