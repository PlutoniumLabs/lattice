package dev.lumentae.lattice.features.motd;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.Mod;
import dev.lumentae.lattice.features.nickname.NicknameManager;
import dev.lumentae.lattice.util.Utils;
import net.minecraft.server.level.ServerPlayer;

public class MotdManager {
    public static void changeMotd() {
        String newMotd = formatMotd(getRandomMotd());
        Mod.getServer().setMotd(newMotd);
    }

    private static String getRandomMotd() {
        if (Config.INSTANCE.motds == null || Config.INSTANCE.motds.isEmpty()) {
            return Mod.getServer().getMotd();
        }
        return Config.INSTANCE.motds.get((int) (Math.random() * Config.INSTANCE.motds.size()));
    }

    private static String formatMotd(String motd) {
        if (!motd.contains("{randomPlayer}")) {
            return motd;
        }

        String playerName;
        if (Mod.getServer().getPlayerList().getPlayers().size() <= 2) {
            if (Config.INSTANCE.offlineMotdPlayerNames == null || Config.INSTANCE.offlineMotdPlayerNames.isEmpty()) {
                playerName = "Player";
            } else {
                playerName = Utils.getRandom(Config.INSTANCE.offlineMotdPlayerNames);
            }
        } else {
            ServerPlayer randomPlayer = Utils.getRandomPlayer();
            assert randomPlayer != null;

            playerName = NicknameManager.getNickname(randomPlayer);
        }

        return motd.replace("{randomPlayer}", playerName);
    }
}
