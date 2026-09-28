package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.Event;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "broadcastSystemMessage(Lnet/minecraft/network/chat/Component;Z)V", at = @At("HEAD"))
    public void lattice$broadcastSystemMessage(Component message, boolean overlay, CallbackInfo ci) {
        Event.OnGameMessage(message);
    }
}
