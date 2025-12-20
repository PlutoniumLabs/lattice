package dev.lumentae.lattice.features.decorator;

import dev.lumentae.lattice.features.nickname.NicknameManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class DecoratorManager implements ChatDecorator {
    public static final ChatDecorator DECORATOR = new DecoratorManager();
    private static final ArrayList<ChatDecorator> decorators = new ArrayList<>();

    public static void registerDecorator(ChatDecorator decorator) {
        decorators.add(decorator);
    }

    @Override
    public @NotNull MutableComponent decorate(@Nullable ServerPlayer serverPlayer, @NotNull Component component) {
        assert serverPlayer != null;

        MutableComponent base = Component.empty();
        for (ChatDecorator decorator : decorators) {
            base.append(decorator.decorate(serverPlayer, component));
        }

        // Append player name
        String playerName = NicknameManager.getNickname(serverPlayer);
        base.append(Component.literal("<")
                .append(
                        Component.literal(playerName)
                        .withStyle(
                                Style.EMPTY.withHoverEvent(new HoverEvent.ShowText(serverPlayer.getName()))
                        )
                )
                .append(Component.literal("> ")
        ).append(component).withStyle(ChatFormatting.RESET));
        return base;
    }
}
