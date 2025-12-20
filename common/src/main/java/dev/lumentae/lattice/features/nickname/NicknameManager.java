package dev.lumentae.lattice.features.nickname;

import dev.lumentae.lattice.Config;
import net.minecraft.server.level.ServerPlayer;

public class NicknameManager {
    public static void setNickname(ServerPlayer player, String nickname) {
        Config.PlayerPlayOptions options = Config.getPlayerPlayOptions(player.getUUID());
        options.nickname = nickname;

        Config.setPlayerPlayOptions(player.getUUID(), options);
    }

    public static String getNickname(ServerPlayer player) {
        String nickName = Config.getPlayerPlayOptions(player.getUUID()).nickname;
        String playerName = player.getName().getString();
        if (nickName == null || nickName.isEmpty()) {
            nickName = playerName;
        }

        if (!nickName.equals(playerName)) {
            nickName = "~" + nickName;
        }
        return nickName;
    }

    public static void removeNickname(ServerPlayer player) {
        Config.PlayerPlayOptions options = Config.getPlayerPlayOptions(player.getUUID());
        options.nickname = "";

        Config.setPlayerPlayOptions(player.getUUID(), options);
    }
}
