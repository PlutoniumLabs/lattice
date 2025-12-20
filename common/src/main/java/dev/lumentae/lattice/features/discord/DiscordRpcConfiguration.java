package dev.lumentae.lattice.features.discord;

public record DiscordRpcConfiguration(boolean enabled, long applicationId, String details, String state,
                                      String largeImageKey,
                                      String largeImageText, String smallImageKey, String smallImageText) {

    public static DiscordRpcConfiguration fromString(String string) {
        String[] split = string.split("\0", -1);
        return new DiscordRpcConfiguration(
                Boolean.parseBoolean(split[0]),
                Long.parseLong(split[1]),
                split[2],
                split[3],
                split[4],
                split[5],
                split[6],
                split[7]
        );
    }

    public String toString() {
        return String.join("\0", Boolean.toString(enabled),
                Long.toString(applicationId),
                details,
                state,
                largeImageKey,
                largeImageText,
                smallImageKey,
                smallImageText);
    }
}