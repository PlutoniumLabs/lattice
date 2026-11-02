package dev.lumentae.lattice.features;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.time.LocalDateTime;

public final class DimensionGate {
    private DimensionGate() {
    }

    public static boolean isClosed(ResourceKey<Level> from, ResourceKey<Level> to) {
        if (from == to) return false;
        return (to == Level.END && Config.INSTANCE.endOpenDate.isAfter(LocalDateTime.now()))
                || (to == Level.NETHER && Config.INSTANCE.netherOpenDate.isAfter(LocalDateTime.now()));
    }

    public static void notifyPlayer(ServerPlayer player, ResourceKey<Level> destination) {
        LocalDateTime openDate = destination == Level.END
                ? Config.INSTANCE.endOpenDate : Config.INSTANCE.netherOpenDate;
        TextUtils.sendMessage(player, Component.translatable("message.lattice.dimension_closed")
                .append(Component.literal(openDate.toString())
                        .withStyle(style -> style.withItalic(true))));
    }
}
