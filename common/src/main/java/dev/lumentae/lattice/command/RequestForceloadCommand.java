package dev.lumentae.lattice.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.coordinates.ColumnPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.util.List;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class RequestForceloadCommand implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("requestforceload")
                .requires(source -> source.getEntity() instanceof ServerPlayer)
                .then(literal("add").then(argument("position", ColumnPosArgument.columnPos())
                        .executes(context -> change(context.getSource().getPlayerOrException(),
                                ColumnPosArgument.getColumnPos(context, "position").toChunkPos(), true))))
                .then(literal("query").then(argument("position", ColumnPosArgument.columnPos())
                        .executes(context -> query(context.getSource().getPlayerOrException(),
                                ColumnPosArgument.getColumnPos(context, "position").toChunkPos()))))
                .then(literal("remove").then(argument("position", ColumnPosArgument.columnPos())
                        .executes(context -> change(context.getSource().getPlayerOrException(),
                                ColumnPosArgument.getColumnPos(context, "position").toChunkPos(), false))))
        );
    }

    private static int query(ServerPlayer player, ChunkPos chunk) {
        boolean loaded = player.level().getForceLoadedChunks().contains(chunk.pack());
        TextUtils.sendMessage(player, Component.translatable(loaded
                ? "message.lattice.requestforceload.query_result_found"
                : "message.lattice.requestforceload.query_result_not_found"));
        return loaded ? Command.SINGLE_SUCCESS : 0;
    }

    private static int change(ServerPlayer player, ChunkPos chunk, boolean add) {
        Config.PlayerPlayOptions options = Config.getPlayerPlayOptions(player.getUUID());
        String dimension = player.level().dimension().identifier().toString();
        List<Long> owned = options.getForceloadedChunks(dimension);
        long position = chunk.pack();

        if (add) {
            if (player.level().getForceLoadedChunks().contains(position)) {
                TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_found"));
                return 0;
            }
            if (options.getForceloadedChunkCount() >= Config.INSTANCE.maxForceloadedChunks) {
                TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.exhausted"));
                return 0;
            }
            if (!player.level().setChunkForced(chunk.x(), chunk.z(), true)) return 0;
            owned.add(position);
        } else {
            if (!owned.contains(position)) {
                TextUtils.sendMessage(player, Component.translatable("message.lattice.requestforceload.query_result_not_found"));
                return 0;
            }
            player.level().setChunkForced(chunk.x(), chunk.z(), false);
            owned.remove(position);
        }

        Config.saveConfig();
        TextUtils.sendMessage(player, Component.translatable(add
                        ? "message.lattice.requestforceload.requested"
                        : "message.lattice.requestforceload.removed",
                options.getForceloadedChunkCount(), Config.INSTANCE.maxForceloadedChunks));
        return Command.SINGLE_SUCCESS;
    }
}
