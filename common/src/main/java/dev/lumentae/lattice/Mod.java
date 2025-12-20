package dev.lumentae.lattice;

import dev.lumentae.lattice.features.decorator.DecoratorManager;
import dev.lumentae.lattice.features.decorator.StatusDecorator;
import dev.lumentae.lattice.features.decorator.TimeDecorator;
import dev.lumentae.lattice.features.discord.webhook.Webhook;
import net.minecraft.server.MinecraftServer;

import java.time.Instant;

public class Mod {
    private static MinecraftServer _server;
    public static boolean viewingRules = false;
    public static final Instant START_TIME = Instant.now();
    public static boolean initialized = false;
    public static Webhook webhook;

    public static MinecraftServer getServer() {
        return _server;
    }
    public static void setServer(MinecraftServer server) {
        _server = server;
    }

    public static void init() {
        if (initialized) return;
        initialized = true;

        Config.configPath.toFile().mkdirs();
        Config.loadConfig();
        DecoratorManager.registerDecorator(new TimeDecorator());
        DecoratorManager.registerDecorator(new StatusDecorator());
        if (!Config.INSTANCE.discordWebhookURL.isEmpty())
            webhook = new Webhook(Config.INSTANCE.discordWebhookURL);

        Constants.LOG.info("Lattice initialized!");
        if (Config.INSTANCE.vanillaMode)
            Constants.LOG.warn("Lattice is running in vanilla mode! Some features will be disabled.");
    }
}
