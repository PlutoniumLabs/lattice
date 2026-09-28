package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "setScreenAndShow", at = @At("HEAD"), cancellable = true)
    public void lattice$setScreen(Screen screen, CallbackInfo ci) {
        if (screen == null && Mod.viewingRules)
            ci.cancel();
    }
}
