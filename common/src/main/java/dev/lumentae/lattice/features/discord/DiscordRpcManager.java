package dev.lumentae.lattice.features.discord;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.*;
import dev.lumentae.lattice.ClientEvent;
import dev.lumentae.lattice.Constants;
import dev.lumentae.lattice.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordRpcManager {
    private static final Timer timer = new Timer();
    public static DiscordRpcConfiguration discordRpcConfiguration;
    private static IPCClient ipcClient;

    public static void initialize(DiscordRpcConfiguration rpcConfiguration) {
        try {
            Constants.LOG.info("Initializing Discord RPC...");
            ipcClient = new IPCClient(rpcConfiguration.applicationId());

            discordRpcConfiguration = rpcConfiguration;
            ipcClient.connect();
            ipcClient.setListener(new IPCListener() {
                @Override
                public void onPacketSent(IPCClient client, Packet packet) {

                }

                @Override
                public void onPacketReceived(IPCClient client, Packet packet) {

                }

                @Override
                public void onActivityJoin(IPCClient client, String secret) {

                }

                @Override
                public void onActivitySpectate(IPCClient client, String secret) {

                }

                @Override
                public void onActivityJoinRequest(IPCClient client, String secret, User user) {

                }

                @Override
                public void onReady(IPCClient client) {
                    Constants.LOG.info("Discord RPC connected!");
                    updateActivity();
                }

                @Override
                public void onClose(IPCClient client, JsonObject json) {

                }

                @Override
                public void onDisconnect(IPCClient client, Throwable t) {

                }
            });

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    updateActivity();
                }
            }, 0, 5000);
        } catch (final Exception e) {
            Constants.LOG.error("Failed to connect to a Discord client.", e);
        }
    }

    public static void updateActivity() {
        try {
            RichPresence.Builder builder = new RichPresence.Builder()
                    .setDetails(discordRpcConfiguration.details())
                    .setState(discordRpcConfiguration.state())
                    .setStartTimestamp(Mod.START_TIME.toEpochMilli())
                    .setActivityType(ActivityType.Playing);

            if (!discordRpcConfiguration.smallImageKey().isEmpty() && !discordRpcConfiguration.smallImageText().isEmpty()) {
                builder.setSmallImage(discordRpcConfiguration.smallImageKey(), discordRpcConfiguration.smallImageText());
            } else if (ClientEvent.client.player != null) {
                builder.setSmallImage("https://crafthead.net/helm/" + ClientEvent.client.player.getUUID(), ClientEvent.client.player.getDisplayName().getString());
            }

            if (!discordRpcConfiguration.largeImageKey().isEmpty() && !discordRpcConfiguration.largeImageText().isEmpty())
                builder.setLargeImage(discordRpcConfiguration.largeImageKey(), discordRpcConfiguration.largeImageText());

            if (ClientEvent.client.getConnection() != null) {
                int maxPlayers = 1;
                String ip = "Singleplayer";
                if (ClientEvent.client.getCurrentServer() != null) {
                    if (ClientEvent.client.getCurrentServer().players != null)
                        maxPlayers = ClientEvent.client.getCurrentServer().players.max();

                    ip = "on " + Objects.requireNonNull(ClientEvent.client.getCurrentServer()).ip;
                }

                assert ClientEvent.client.player != null;
                builder
                        .setParty("party", ClientEvent.client.getConnection().getOnlinePlayers().size(), maxPlayers, PartyPrivacy.Public)
                        .setState("Playing " + ip)
                        .setDetails(getCorrectDimensionString(ClientEvent.client.player.level().dimension())
                                + " "
                                + Component.translatable("message.lattice.discord.at_coords",
                                        (int) ClientEvent.client.player.getX(),
                                        (int) ClientEvent.client.player.getY(),
                                        (int) ClientEvent.client.player.getZ()
                                ).getString()
                        );
            }

            RichPresence richPresence = builder.build();
            ipcClient.sendRichPresence(richPresence);
        } catch (final Exception e) {
            Constants.LOG.error("Failed to update Discord RPC activity", e);
        }
    }

    private static String getCorrectDimensionString(ResourceKey<Level> dimension) {
        switch (dimension.location().toString()) {
            case "minecraft:overworld":
                return Component.translatable("message.lattice.discord.in_overworld").getString();
            case "minecraft:the_nether":
                return Component.translatable("message.lattice.discord.in_nether").getString();
            case "minecraft:the_end":
                return Component.translatable("message.lattice.discord.in_end").getString();
        }
        return dimension.location().toString();
    }
}
