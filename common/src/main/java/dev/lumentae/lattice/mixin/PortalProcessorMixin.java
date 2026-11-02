package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.features.DimensionGate;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PortalProcessor.class)
public class PortalProcessorMixin {
    @Inject(method = "getPortalDestination", at = @At("RETURN"), cancellable = true)
    private void lattice$blockClosedDimension(ServerLevel serverLevel, Entity entity, CallbackInfoReturnable<TeleportTransition> cir) {
        TeleportTransition transition = cir.getReturnValue();
        if (transition == null) return;
        ResourceKey<Level> newDimension = transition.newLevel().dimension();
        if (DimensionGate.isClosed(serverLevel.dimension(), newDimension)) {
            if (entity instanceof ServerPlayer player) {
                DimensionGate.notifyPlayer(player, newDimension);
            }
            cir.setReturnValue(null);
        }
    }
}
