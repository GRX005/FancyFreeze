package _1ms.FF;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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

        new Metrics(this, 29773);
        Thread.ofVirtual().name("UpdateChecker").start(this::checkForUpdates);

        getLogger().info("FancyFreeze has been loaded.");
    }

    @Override
    public void onDisable() {
        Freezer.unFreeze(null,null);
        getLogger().info("FancyFreeze has been unloaded.");
    }

    private void handleParticle() {
        final double radius = 1.5;
        final int points = 16;
        final double[] xOff = new double[points], zOff = new double[points];

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            xOff[i] = radius * Math.cos(angle);
            zOff[i] = radius * Math.sin(angle);
        }
        var pBuilder = Particle.DUST.builder().data(new Particle.DustOptions(Color.WHITE, 1));

        final int[] particleTick = {0};
        Bukkit.getScheduler().runTaskTimer(Main.pl, t -> {
            if (freezed.isEmpty()) return;
            final int i = particleTick[0];
            particleTick[0] = (i + 1) % points;
            freezed.forEach(p->{
                var target = Objects.requireNonNull(Bukkit.getPlayer(p));

                var x = target.getX() + xOff[i];
                var y = target.getY() + 2.0;
                var z = target.getZ() + zOff[i];

                // Spawn particle
                pBuilder.location(target.getWorld(),x,y,z).spawn();
            });
        }, 0, 1);
    }

    private void checkForUpdates(){
        final String currV = getPluginMeta().getVersion();
        final String latestV = getLatest(currV);

        if (!currV.equals(latestV))
            getLogger().warning("New version available: "+ latestV+". You are still on "+ currV+".");
    }

    private String getLatest(String v){
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> resp = client.send(
                    HttpRequest.newBuilder()
                            .uri(URI.create("https://api.modrinth.com/v2/project/fancyfreeze/version"))
                            .header("User-Agent","FancyFreeze "+v+" (https://github.com/GRX005/FancyFreeze)")
                            .GET()
                            .build(),
                    HttpResponse.BodyHandlers.ofString());
            JsonArray respArr = new Gson().fromJson(resp.body(), JsonArray.class);
            return respArr.get(0).getAsJsonObject().get("version_number").getAsString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error while searching for updates.",e);
        }
    }

}
