package dev.lumentae.lattice.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.features.status.StatusManager;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class StatusCommand implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("status")
                .then(argument("status", StringArgumentType.greedyString())
                        .suggests((context, builder) -> {
                            builder.suggest("reset");
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String status = StringArgumentType.getString(commandContext, "status");

                            ServerPlayer player = commandContext.getSource().getPlayer();
                            assert player != null;

                            if (status.equals("reset")) {
                                StatusManager.removeStatus(player);
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.status.removed").withStyle(ChatFormatting.GREEN));
                                return Command.SINGLE_SUCCESS;
                            }

                            StatusManager.setStatus(player, status);
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.status.set").append(
                                    TextUtils.parseColoredText(status)
                            ));

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();
                    assert player != null;

                    String status = Config.getPlayerPlayOptions(player.getUUID()).status;
                    if (status == null) {
                        TextUtils.sendMessage(player, Component.translatable("message.lattice.status.none").withStyle(ChatFormatting.RED));
                    } else {
                        TextUtils.sendMessage(player, Component.translatable("message.lattice.status.show").append(
                                Component.literal(status)
                        ));
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}
