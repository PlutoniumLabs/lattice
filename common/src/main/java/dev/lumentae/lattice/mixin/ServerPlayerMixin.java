package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.features.DimensionGate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "teleport(Lnet/minecraft/world/level/portal/TeleportTransition;)Lnet/minecraft/server/level/ServerPlayer;", at = @At("HEAD"), cancellable = true)
    private void lattice$blockClosedDimension(TeleportTransition transition, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if (DimensionGate.isClosed(player.level().dimension(), transition.newLevel().dimension())) {
            DimensionGate.notifyPlayer(player, transition.newLevel().dimension());
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    public void canHarmPlayer(Player target, CallbackInfoReturnable<Boolean> cir) {
        boolean thisPvP = Config.getPlayerPlayOptions(((ServerPlayer) (Object) this).getUUID()).enablePvP;
        boolean otherPvP = Config.getPlayerPlayOptions(target.getUUID()).enablePvP;

        if (!thisPvP || !otherPvP) {
            cir.setReturnValue(false);
        }
    }
}
