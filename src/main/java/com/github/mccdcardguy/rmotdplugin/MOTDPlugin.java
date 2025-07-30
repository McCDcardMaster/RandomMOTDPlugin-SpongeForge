package com.github.mccdcardguy.rmotdplugin;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.nio.file.Path;

@Plugin(
        id = "rmotdplugin",
        name = "Random MOTD Plugin",
        version = "1.0",
        description = "Displays random MOTD messages"
)
public class MOTDPlugin {

    private static MOTDPlugin instance;
    private MOTDManager motdManager;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;

    @Inject
    private PluginContainer container;

    @Listener
    public void onInit(GameInitializationEvent event) {
        instance = this;
        
        // Инициализация менеджера MOTD
        motdManager = new MOTDManager(configDir);
        motdManager.loadMessages();
        
        // Регистрация слушателя
        Sponge.getEventManager().registerListeners(
                this, 
                new com.github.mccdcardguy.rmotdplugin.Listener(motdManager)
        );
        
        logger.info("Random MOTD Plugin has been initialized!");
    }

    public static MOTDPlugin getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return logger;
    }

    public MOTDManager getMotdManager() {
        return motdManager;
    }
}