package dev.lumentae.lattice.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.features.nickname.NicknameManager;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class NickCommand implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("nick")
                .then(argument("nickname", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            assert player != null;

                            builder.suggest(player.getName().getString());
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String nickname = StringArgumentType.getString(commandContext, "nickname");

                            ServerPlayer player = commandContext.getSource().getPlayer();
                            assert player != null;

                            if (nickname.equals(player.getName().getString())) {
                                NicknameManager.removeNickname(player);
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.removed").withStyle(ChatFormatting.GREEN));
                                return Command.SINGLE_SUCCESS;
                            }

                            NicknameManager.setNickname(player, nickname);
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.set").append(
                                    Component.literal(nickname).withStyle(ChatFormatting.GREEN)
                            ));

                            return Command.SINGLE_SUCCESS;
                        })
                )
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayer();
                    assert player != null;

                    String name = Config.getPlayerPlayOptions(player.getUUID()).nickname;
                    if (name == null) {
                        TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.none").withStyle(ChatFormatting.RED));
                    } else {
                        TextUtils.sendMessage(player, Component.translatable("message.lattice.nickname.show").append(
                                Component.literal(name).withStyle(ChatFormatting.GREEN)
                        ));
                    }
                    return Command.SINGLE_SUCCESS;
                })
        );
    }
}
