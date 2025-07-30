package com.github.mccdcardguy.rmotdplugin;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MOTDManager {

    private final Path configDir;
    private final List<String> defaultMessages;
    private List<String> messages;
    private final Random random = new Random();

    public MOTDManager(Path configDir) {
        this.configDir = configDir;

        this.defaultMessages = new ArrayList<String>() {{
            add("&7&oYo! what's up?");
            add("&7&oHello my boy!");
            add("&7&oYes, plugin is work right?");
        }};
    }

    public void loadMessages() {
        Path configFile = configDir.resolve("randommotd.conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setPath(configFile)
                .setDefaultOptions(ConfigurationOptions.defaults())
                .build();

        try {
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
            }

            ConfigurationNode node;

            if (!Files.exists(configFile)) {
                Files.createFile(configFile);
                node = loader.createEmptyNode();
                messages = new ArrayList<>(defaultMessages);
                saveMessages();
            } else {
                node = loader.load();
                messages = node.getNode("messages").getList(TypeToken.of(String.class), defaultMessages);
            }

        } catch (IOException | ObjectMappingException e) {
            MOTDPlugin.getInstance().getLogger().error("Error loading MOTD config: " + e.getMessage());
            messages = new ArrayList<>(defaultMessages);
        }
    }

    public void saveMessages() {
        Path configFile = configDir.resolve("randommotd.conf");
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder()
                .setPath(configFile)
                .build();

        try {
            ConfigurationNode node = loader.createEmptyNode();
            node.getNode("messages").setValue(new TypeToken<List<String>>() {}, messages);
            loader.save(node);
        } catch (IOException | ObjectMappingException e) {
            MOTDPlugin.getInstance().getLogger().error("Error saving MOTD config: " + e.getMessage());
        }
    }

    public void addMessage(String message) {
        messages.add(message);
        saveMessages();
    }

    public void removeMessage(int index) {
        if (index >= 0 && index < messages.size()) {
            messages.remove(index);
            saveMessages();
        }
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

    public Text getRandomMotd() {
        if (messages.isEmpty()) {
            return Text.of("Default MOTD");
        }

        String rawMessage = messages.get(random.nextInt(messages.size()));
        return TextSerializers.FORMATTING_CODE.deserialize(rawMessage);
    }
}