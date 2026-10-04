package dev.lumentae.lattice;

import dev.lumentae.lattice.features.discord.DiscordRpcConfiguration;
import dev.lumentae.lattice.features.discord.DiscordRpcManager;
import dev.lumentae.lattice.packet.ClientboundConfigurationPacket;
import net.minecraft.client.Minecraft;

public class ClientEvent {
    public static Minecraft client;

    public static void OnConfigurationPacket(ClientboundConfigurationPacket data) {
        DiscordRpcConfiguration rpcConfiguration = DiscordRpcConfiguration.fromString(data.discordRpcConfiguration());
        DiscordRpcManager.discordRpcConfiguration = rpcConfiguration;
        DiscordRpcManager.updateActivity();
    }

    public static void OnClientStarted(Minecraft client) {
        ClientEvent.client = client;
        DiscordRpcManager.initialize(Config.INSTANCE.discordRpcConfiguration);
    }
}
