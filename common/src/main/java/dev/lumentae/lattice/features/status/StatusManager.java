package dev.lumentae.lattice.features.status;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class StatusManager {
    public static void setStatus(ServerPlayer player, String status) {
        Config.PlayerPlayOptions options = Config.getPlayerPlayOptions(player.getUUID());
        options.status = status;

        Config.setPlayerPlayOptions(player.getUUID(), options);
    }

    public static Component getStatus(ServerPlayer player) {
        String status = Config.getPlayerPlayOptions(player.getUUID()).status;
        return TextUtils.parseColoredText(status);
    }

    public static void removeStatus(ServerPlayer player) {
        Config.PlayerPlayOptions options = Config.getPlayerPlayOptions(player.getUUID());
        options.status = "";

        Config.setPlayerPlayOptions(player.getUUID(), options);
    }
}
