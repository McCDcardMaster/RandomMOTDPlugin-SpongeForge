package com.github.mccdcardguy.rmotdplugin;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.text.Text;

public class Listener {

    private final MOTDManager motdManager;

    public Listener(MOTDManager motdManager) {
        this.motdManager = motdManager;
    }

    @org.spongepowered.api.event.Listener
    public void onServerPing(ClientPingServerEvent event) {
        Text randomMotd = motdManager.getRandomMotd();
        event.getResponse().setDescription(randomMotd);

        event.getResponse().getPlayers().ifPresent(players -> {
            players.setOnline(Sponge.getServer().getOnlinePlayers().size());
        });
    }
}