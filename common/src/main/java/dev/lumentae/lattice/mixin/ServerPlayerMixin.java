package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.Config;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
    @Inject(method = "canHarmPlayer", at = @At("HEAD"), cancellable = true)
    public void canHarmPlayer(Player target, CallbackInfoReturnable<Boolean> cir) {
        boolean thisPvP = Config.getPlayerPlayOptions(((ServerPlayer) (Object) this).getUUID()).enablePvP;
        boolean otherPvP = Config.getPlayerPlayOptions(target.getUUID()).enablePvP;

        if (!thisPvP || !otherPvP) {
            cir.setReturnValue(false);
        }
    }
}
