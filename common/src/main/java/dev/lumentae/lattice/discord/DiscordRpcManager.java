package dev.lumentae.lattice.discord;

import dev.caoimhe.jdiscordipc.JDiscordIPC;
import dev.caoimhe.jdiscordipc.activity.model.Activity;
import dev.caoimhe.jdiscordipc.activity.model.ActivityBuilder;
import dev.caoimhe.jdiscordipc.activity.model.ActivityTimestamps;
import dev.caoimhe.jdiscordipc.activity.model.ActivityType;
import dev.caoimhe.jdiscordipc.activity.model.party.ActivityPartyPrivacy;
import dev.caoimhe.jdiscordipc.event.DiscordEventListener;
import dev.caoimhe.jdiscordipc.event.model.ReadyEvent;
import dev.caoimhe.jdiscordipc.modern.socket.ModernSystemSocketFactory;
import dev.lumentae.lattice.ClientEvent;
import dev.lumentae.lattice.Constants;
import dev.lumentae.lattice.Mod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordRpcManager implements DiscordEventListener {
    private static final Timer timer = new Timer();
    public static DiscordRpcConfiguration discordRpcConfiguration;
    private static JDiscordIPC jDiscordIPC;
    private static Activity activity;

    public static void initialize(DiscordRpcConfiguration rpcConfiguration) {
        try {
            Constants.LOG.info("Initializing Discord RPC...");
            jDiscordIPC = JDiscordIPC.builder(rpcConfiguration.applicationId())
                    .systemSocketFactory(new ModernSystemSocketFactory())
                    .build();

            discordRpcConfiguration = rpcConfiguration;
            jDiscordIPC.connect();
            jDiscordIPC.registerEventListener(new DiscordRpcManager());

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
            ActivityBuilder activityBuilder = Activity.builder()
                    .details(discordRpcConfiguration.details())
                    .state(discordRpcConfiguration.state())
                    .assets((assets) -> {
                        if (!discordRpcConfiguration.smallImageKey().isEmpty() && !discordRpcConfiguration.smallImageText().isEmpty()) {
                            assets.smallImage(discordRpcConfiguration.smallImageKey(), discordRpcConfiguration.smallImageText());
                        } else if (ClientEvent.client.player != null) {
                            assets.smallImage("https://crafthead.net/helm/" + ClientEvent.client.player.getUUID(), ClientEvent.client.player.getDisplayName().getString());
                        }

                        if (!discordRpcConfiguration.largeImageKey().isEmpty() && !discordRpcConfiguration.largeImageText().isEmpty())
                            assets.largeImage(discordRpcConfiguration.largeImageKey(), discordRpcConfiguration.largeImageText());
                    })
                    .timestamps(ActivityTimestamps.from(Mod.START_TIME))
                    .type(ActivityType.PLAYING);

            if (ClientEvent.client.getConnection() != null) {
                int maxPlayers;
                String ip = "Singleplayer";
                if (ClientEvent.client.getCurrentServer() != null) {
                    if (ClientEvent.client.getCurrentServer().players != null)
                        maxPlayers = ClientEvent.client.getCurrentServer().players.max();
                    else {
                        maxPlayers = 1;
                    }
                    ip = "on " + Objects.requireNonNull(ClientEvent.client.getCurrentServer()).ip;
                } else {
                    maxPlayers = 1;
                }

                assert ClientEvent.client.player != null;
                activityBuilder = activityBuilder
                        .party("party", ClientEvent.client.getConnection().getOnlinePlayers().size(), (builder) -> {
                            builder.maximumSize(maxPlayers);
                            builder.privacy(ActivityPartyPrivacy.PUBLIC);
                        })
                        .state("Playing " + ip)
                        .details(getCorrectDimensionString(ClientEvent.client.player.level().dimension())
                                + " "
                                + Component.translatable("message.lattice.discord.at_coords",
                                        (int) ClientEvent.client.player.getX(),
                                        (int) ClientEvent.client.player.getY(),
                                        (int) ClientEvent.client.player.getZ()
                                ).getString()
                        );
            }

            activity = activityBuilder.build();
            jDiscordIPC.updateActivity(activity);
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

    @Override
    public void onReadyEvent(ReadyEvent event) {
        Constants.LOG.info("Connected to Discord RPC.");
        jDiscordIPC.updateActivity(activity);
    }
}
