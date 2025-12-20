package dev.lumentae.lattice.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.lumentae.lattice.features.home.Home;
import dev.lumentae.lattice.features.home.HomeManager;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class HomeCommand implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("home")
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            assert player != null;

                            for (Home home : HomeManager.getHomes(player)) {
                                builder.suggest(home.name());
                            }
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String name = StringArgumentType.getString(commandContext, "name");

                            ServerPlayer player = commandContext.getSource().getPlayer();
                            assert player != null;

                            Home home = HomeManager.getHome(name, player);
                            if (home == null) {
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.home.not_found", name).withStyle(ChatFormatting.RED));
                                return 0;
                            }
                            HomeManager.teleportToHome(home, player);
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.home.teleported", home.name()));
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        dispatcher.register(literal("sethome")
                .then(argument("name", StringArgumentType.word())
                        .executes(commandContext -> {
                            String name = StringArgumentType.getString(commandContext, "name");

                            ServerPlayer player = commandContext.getSource().getPlayer();
                            assert player != null;

                            Home home = HomeManager.getHome(name, player);
                            if (home != null) {
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.home.exists", name).withStyle(ChatFormatting.RED));
                                return 0;
                            }
                            home = HomeManager.createHome(name, player);
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.home.created", home.name()));
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
        dispatcher.register(literal("delhome")
                .then(argument("name", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            assert player != null;

                            for (Home home : HomeManager.getHomes(player)) {
                                builder.suggest(home.name());
                            }
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            String name = StringArgumentType.getString(commandContext, "name");

                            ServerPlayer player = commandContext.getSource().getPlayer();
                            assert player != null;

                            Home home = HomeManager.getHome(name, player);
                            if (home == null) {
                                TextUtils.sendMessage(player, Component.translatable("message.lattice.home.not_found", name).withStyle(ChatFormatting.RED));
                                return 0;
                            }
                            HomeManager.deleteHome(home, player);
                            TextUtils.sendMessage(player, Component.translatable("message.lattice.home.deleted", home.name()));
                            return Command.SINGLE_SUCCESS;
                        })
                )
        );
    }
}
