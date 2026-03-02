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

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static _1ms.FF.CfgMgr.*;
import static _1ms.FF.EventMgr.freezed;

public final class Main extends JavaPlugin {

    public static Main pl;
    public static MiniMessage mm;
    public static NamespacedKey flyPerms;

    @Override
    public void onEnable() {
        pl=this;
        flyPerms = new NamespacedKey(this, "flyPerms");
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

        if (getConfig().getBoolean("FancyFreeze_Config.Features.BSTATS")) {
            var m = new Metrics(this, 29773);
            m.addCustomChart(new SimplePie("cfg_features", ()-> String.join(";",
                    "SPAWN_PARTICLE="     + SPAWN_PARTICLE,
                    "SPAWN_EFFECT="       + SPAWN_EFFECT,
                    "GLOWING="            + GLOWING,
                    "ACTION_BAR_MSG="     + ACTION_BAR_MSG,
                    "BLOCK_PLAYER_MOVE="  + BLOCK_PLAYER_MOVE,
                    "BLOCK_PLAYER_INTERACT=" + BLOCK_PLAYER_INTERACT,
                    "BLOCK_PLAYER_HIT="   + BLOCK_PLAYER_HIT,
                    "BLOCK_ITEM_DROP="    + BLOCK_ITEM_DROP,
                    "BLOCK_ITEM_PICKUP="  + BLOCK_ITEM_PICKUP,
                    "BLOCK_PLAYER_COMMANDS=" + BLOCK_PLAYER_COMMANDS,
                    "BLOCK_INTERACTING_WITH_ENTITY=" + BLOCK_INTERACTING_WITH_ENTITY,
                    "BLOCK_INVENTORY_CLICK=" + BLOCK_INVENTORY_CHANGES,
                    "BLOCK_PLAYER_DAMAGE=" + BLOCK_PLAYER_DAMAGE,
                    "BLOCK_ITEM_SWAP="    + BLOCK_ITEM_SWAP,
                    "BLOCK_GUI_OPEN="     + BLOCK_GUI_OPEN,
                    "BLOCK_TELEPORT="     + BLOCK_TELEPORT,
                    "BLOCK_CHAT="         + BLOCK_CHAT,
                    "CMD_ON_LEAVE_USED="+ !CMD_ON_LEAVE.isEmpty(),
                    "COMMANDS_WHITELIST_USED="+ !CMD_WHITELIST.isEmpty()
            )));
        }

        if (getConfig().getBoolean("FancyFreeze_Config.Features.CHECK_FOR_UPDATES"))
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
                var target = Bukkit.getPlayer(p);
                if (target == null) return;

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

        if (latestV!=null&&!currV.equals(latestV))
            getLogger().warning("A new version is available: "+ latestV+". You are still on "+ currV+".");
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
        } catch (Exception e) {
            getLogger().warning("Error occurred while checking for updates: "+e);
            return null;
        }
    }

}
