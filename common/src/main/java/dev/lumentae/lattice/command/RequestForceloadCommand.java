package dev.lumentae.lattice.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.core.jmx.Server;

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

                                    ChunkPos playerChunkPos = player.chunkPosition();
                                    builder.suggest(playerChunkPos.x() + " " + playerChunkPos.z());

                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String action = StringArgumentType.getString(context, "action");

                                    ServerPlayer player = context.getSource().getPlayer();
                                    assert player != null;

                                    ColumnPos columnPos = ColumnPosArgument.getColumnPos(context, "chunk");
                                    switch (action) {
                                        case "add":
                                            if (!isChunkForceLoaded(player.level(), columnPos)) {
                                                if (canPlayerLoadMoreChunks(player)) {
                                                    player.level().setChunkForced(columnPos.x(), columnPos.z(), true);
                                                    Config.getPlayerPlayOptions(player.getUUID()).forceloadedChunks.add(columnPos.toLong());

                                                    TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.requested",
                                                            Config.getPlayerPlayOptions(player.getUUID()).forceloadedChunks.size(),
                                                            Config.INSTANCE.maxForceloadedChunks));
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                                TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.exhausted"));
                                                return 0;
                                            }
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_found"));
                                            break;

                                        case "query":
                                            if (isChunkForceLoaded(player.level(), columnPos)) {
                                                TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_found"));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_not_found"));
                                            break;

                                        case "remove":
                                            if (Config.getPlayerPlayOptions(player.getUUID()).forceloadedChunks.contains(columnPos.toLong())) {
                                                player.level().setChunkForced(columnPos.x(), columnPos.z(), false);
                                                Config.getPlayerPlayOptions(player.getUUID()).forceloadedChunks.remove(columnPos.toLong());

                                                TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.removed",
                                                        Config.getPlayerPlayOptions(player.getUUID()).forceloadedChunks.size(),
                                                        Config.INSTANCE.maxForceloadedChunks));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                            TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_not_found"));
                                            return 0;
                                    }

                                    return 0;
                                })
                        )
                )
        );
    }

    public boolean isChunkForceLoaded(ServerLevel level, ColumnPos columnPos) {
        AtomicBoolean found = new AtomicBoolean(false);
        level.getForceLoadedChunks().forEach(chunkPos -> {
            if (ChunkPos.unpack(chunkPos).equals(new ChunkPos(columnPos.x(), columnPos.z()))) {
                found.set(true);
            }
        });
        if (found.get()) {
            return true;
        }
        return false;
    }

    public boolean canPlayerLoadMoreChunks(ServerPlayer player) {
        return Config.getPlayerPlayOptions(player.getUUID()).forceloadedChunks.size() < Config.INSTANCE.maxForceloadedChunks;
    }
}
