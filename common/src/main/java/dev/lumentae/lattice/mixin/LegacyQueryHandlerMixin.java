package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.features.motd.MotdManager;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.server.network.LegacyQueryHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LegacyQueryHandler.class)
public abstract class LegacyQueryHandlerMixin {
    @Inject(method = "channelRead", at = @At("HEAD"))
    private void lattice$channelRead(ChannelHandlerContext context, Object message, CallbackInfo ci) {
        try {
            MotdManager.changeMotd();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
