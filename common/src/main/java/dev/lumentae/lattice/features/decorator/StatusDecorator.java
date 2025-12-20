package dev.lumentae.lattice.features.decorator;

import dev.lumentae.lattice.features.status.StatusManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StatusDecorator implements ChatDecorator {
    @Override
    public @NotNull MutableComponent decorate(@Nullable ServerPlayer serverPlayer, @NotNull Component component) {
        if (serverPlayer == null) {
            return Component.literal("");
        }

        Component status = StatusManager.getStatus(serverPlayer);
        if (status.getString().isEmpty()) {
            return Component.literal("");
        }

        return Component.literal("[")
                .append(status)
                .append(Component.literal("] "))
                .withStyle(ChatFormatting.RESET);
    }
}
