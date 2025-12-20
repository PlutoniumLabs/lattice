package dev.lumentae.lattice.mixin;

import com.mojang.brigadier.ParseResults;
import dev.lumentae.lattice.features.commandlogger.CommandLogger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandsMixin {
    @Inject(method = "performCommand", at = @At("HEAD"))
    public void lattice$performCommand(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
        CommandSourceStack commandSourceStack = parseResults.getContext().getSource();
        if (commandSourceStack.getEntity() instanceof ServerPlayer serverPlayer)
            CommandLogger.logCommand(serverPlayer, command);
    }
}
