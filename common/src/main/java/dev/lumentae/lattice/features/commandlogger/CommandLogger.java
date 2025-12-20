package dev.lumentae.lattice.features.commandlogger;

import dev.lumentae.lattice.Constants;
import net.minecraft.server.level.ServerPlayer;

public class CommandLogger {
    public static void logCommand(ServerPlayer source, String command) {
        Constants.LOG.info("{} executed command: {}", source.getName().getString(), command);
    }
}
