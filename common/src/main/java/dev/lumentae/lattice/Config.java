package dev.lumentae.lattice;

import com.google.gson.*;
import dev.lumentae.lattice.features.discord.DiscordRpcConfiguration;
import dev.lumentae.lattice.features.home.Home;
import dev.lumentae.lattice.platform.Services;
import dev.lumentae.lattice.util.TextUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Config {
    /**
     * A map of player UUIDs to their play options
     * <p>
     * If a player does not have any options set, the default options will be used
     */
    public Map<UUID, PlayerPlayOptions> playerOptions = new HashMap<>();

    /**
     * The default play options for players
     * <p>
     * These options will be used if a player does not have any option set
     */
    public static PlayerPlayOptions DEFAULT_PLAY_OPTIONS = new PlayerPlayOptions();

    /**
     * The date when the server will be opened
     */
    public LocalDateTime serverOpenDate = LocalDateTime.now();

    /**
     * The date when the end will be opened
     * <p>
     * This is set to 10 days after the server is opened
     */
    public LocalDateTime endOpenDate = serverOpenDate.plusSeconds(60 * 60 * 24 * 10);

    /**
     * A list of illegal mods that should not be allowed
     */
    public List<String> illegalMods = Arrays.asList(
            "cheatbreaker",
            "impact",
            "lunar",
            "meteor",
            "nebulous",
            "salhack",
            "wurst",
            "future",
            "aristois",
            "bape",
            "xray",
            "x-ray"
    );

    /**
     * A list of mods that should be allowlisted
     */
    public List<String> allowedMods = List.of(
            "antixray"
    );

    /**
     * A list of MOTDs to be displayed to players when they join the server
     */
    public ArrayList<String> motds;

    /**
     * A list of player names to be used in MOTDs when a player is offline
     */
    public ArrayList<String> offlineMotdPlayerNames;

    /*
     * A list of rules to be displayed to players when they join the server
     */
    public ArrayList<String> rules = new ArrayList<>();

    /*
     * Discord RPC configuration
     */
    public DiscordRpcConfiguration discordRpcConfiguration = new DiscordRpcConfiguration(
            true,
            1439275936955633724L,
            "Not playing...",
            "",
            "",
            "",
            "",
            ""
    );

    /**
     * The date when the nether will be opened
     * <p>
     * This is set to 2 days after the server is opened
     */
    public LocalDateTime netherOpenDate = serverOpenDate.plusSeconds(60 * 60 * 24 * 2);

    /**
     * Whether the server is in vanilla mode (no extra features)
     */
    public boolean vanillaMode = false;

    /*
     * Whether dispensers use durability
     */
    public boolean dispenserUsesDurability = true;

    /*
     * The Discord webhook URL to send messages to
     */
    public String discordWebhookURL = "";

    /*
     * Whether the custom dispenser behavior should be enabled
     */
    public boolean enableDispenserBehavior = true;

    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static class PlayerPlayOptions {
        /**
         * Whether the player is allowed to use PvP
         * <p>
         * This is true by default
         */
        public boolean enablePvP = true;

        /*
         * The player's nickname
         */
        public String nickname = null;

        /*
         * The player's status message
         */
        public String status = "";

        /**
         * Whether the player has accepted the rules
         */
        public boolean acceptedRules = false;

        /**
         * The player's homes
         */
        public List<Home> homes = new ArrayList<>();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Config INSTANCE = new Config();

    public static Path configPath = Services.PLATFORM.getConfigDirectory();
    public static Path configFilePath = configPath.resolve("config.json");

    public static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, jsonDeserializationContext)
                    -> TextUtils.parseDateString(json.getAsJsonPrimitive().getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                    -> new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
            .create();

    public static void saveConfig() {
        try {
            Files.writeString(configFilePath, GSON.toJson(INSTANCE));
        } catch (IOException ignored) {
        }
    }

    public static void loadConfig() {
        try {
            // Create a config file if it doesn't exist
            if (!configFilePath.toFile().exists()) {
                Files.writeString(configFilePath, GSON.toJson(new Config()));
            }

            INSTANCE = GSON.fromJson(Files.readString(configFilePath), Config.class);
        } catch (IOException ignored) {
        }
    }

    public static PlayerPlayOptions getPlayerPlayOptions(UUID uuid) {
        return INSTANCE.playerOptions.computeIfAbsent(uuid, k -> new PlayerPlayOptions());
    }

    public static void setPlayerPlayOptions(UUID uuid, PlayerPlayOptions options) {
        INSTANCE.playerOptions.put(uuid, options);
        saveConfig();
    }
}