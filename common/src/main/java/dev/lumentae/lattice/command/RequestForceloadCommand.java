package dev.lumentae.lattice.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.concurrent.atomic.AtomicBoolean;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RequestForceloadCommand implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("requestforceload")
                .then(argument("action", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            assert player != null;

                            builder.suggest("add");
                            builder.suggest("query");
                            builder.suggest("remove");
                            return builder.buildFuture();
                        })
                        .executes(context -> {
                            String action = StringArgumentType.getString(context, "action");
                            ServerPlayer player = context.getSource().getPlayer();
                            assert player != null;

                            TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.invalid_action", action));
                            return 0;
                        })
                        .then(argument("chunk", ColumnPosArgument.columnPos())
                                .suggests((context, builder) -> {
                                    ServerPlayer player = context.getSource().getPlayer();
                                    assert player != null;

                                    builder.suggest(player.chunkPosition().toString());

                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String action = StringArgumentType.getString(context, "action");

                                    ServerPlayer player = context.getSource().getPlayer();
                                    assert player != null;

                                    ColumnPos columnPos = ColumnPosArgument.getColumnPos(context, "chunk");
                                    switch (action) {
                                        case "add":
                                            player.level().setChunkForced(columnPos.x(), columnPos.z(), true);
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.requested"));
                                            return Command.SINGLE_SUCCESS;

                                        case "query":
                                            AtomicBoolean found = new AtomicBoolean(false);
                                            player.level().getForceLoadedChunks().forEach(chunkPos -> {
                                                if (ChunkPos.unpack(chunkPos).equals(new ChunkPos(columnPos.x(), columnPos.z()))) {
                                                    found.set(true);
                                                }
                                            });
                                            if (found.get()) {
                                                TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_found"));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_not_found"));
                                            break;

                                        case "remove":
                                            player.level().setChunkForced(columnPos.x(), columnPos.z(), false);
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.removed"));
                                            return Command.SINGLE_SUCCESS;
                                    }

                                    return 0;
                                })
                        )
                )
        );
    }
}
