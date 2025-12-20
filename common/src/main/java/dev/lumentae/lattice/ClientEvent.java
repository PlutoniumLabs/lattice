package dev.lumentae.lattice;

import dev.lumentae.lattice.features.discord.DiscordRpcConfiguration;
import dev.lumentae.lattice.features.discord.DiscordRpcManager;
import dev.lumentae.lattice.packet.ClientboundConfigurationPacket;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class ClientEvent {
    public static Minecraft client;

    public static void OnConfigurationPacket(ClientboundConfigurationPacket data) {
        List<String> ruleStrings = List.of(data.rules().split("\0"));
        List<Component> rules = new ArrayList<>();
        for (String rule : ruleStrings) {
            Component parsed = TextUtils.fromString(rule);
            if (parsed == null) {
                Constants.LOG.warn("Failed to parse rule: {}", rule);
                continue;
            }
            rules.add(parsed);
        }

        if (!rules.isEmpty()) {
            BookViewScreen.BookAccess bookviewscreen$bookaccess = new BookViewScreen.BookAccess(rules);
            Mod.viewingRules = true;
            Minecraft.getInstance().setScreen(new BookViewScreen(bookviewscreen$bookaccess));
        }

        DiscordRpcConfiguration rpcConfiguration = DiscordRpcConfiguration.fromString(data.discordRpcConfiguration());
        DiscordRpcManager.discordRpcConfiguration = rpcConfiguration;
        DiscordRpcManager.updateActivity();
    }

    public static void OnClientStarted(Minecraft client) {
        ClientEvent.client = client;
        DiscordRpcManager.initialize(Config.INSTANCE.discordRpcConfiguration);
    }
}
