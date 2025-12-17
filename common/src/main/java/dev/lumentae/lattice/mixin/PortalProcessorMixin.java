package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.util.TextUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalDateTime;

@Mixin(PortalProcessor.class)
public class PortalProcessorMixin {
    @Shadow
    private Portal portal;

    @Shadow
    private BlockPos entryPosition;

    @Inject(method = "getPortalDestination", at = @At("HEAD"), cancellable = true)
    private void init(ServerLevel level, Entity entity, CallbackInfoReturnable<TeleportTransition> cir) {
        TeleportTransition transition = this.portal.getPortalDestination(level, entity, this.entryPosition);
        assert transition != null;

        ResourceKey<Level> newDimension = transition.newLevel().dimension();
        boolean isInClosedDimension = (
                newDimension == Level.END && Config.INSTANCE.endOpenDate.isAfter(LocalDateTime.now())
        ) || (
                newDimension == Level.NETHER && Config.INSTANCE.netherOpenDate.isAfter(LocalDateTime.now())
        );

        if (isInClosedDimension) {
            if (entity instanceof ServerPlayer player) {
                sendErrorMessage(player, newDimension);
            }
            cir.setReturnValue(null);
        }
    }

    private static void sendErrorMessage(ServerPlayer player, ResourceKey<Level> newDimension) {
        String date = newDimension == Level.END
                ? String.valueOf(Config.INSTANCE.endOpenDate)
                : String.valueOf(Config.INSTANCE.netherOpenDate);

        TextUtils.sendMessage(player, Component.translatable("message.lattice.dimension_closed")
                .append(Component.literal(date)
                        .withStyle(
                                (style) -> style.withItalic(true)
                        )
                )
        );
    }
}
