package dev.lumentae.lattice.mixin;

import dev.lumentae.lattice.Mod;
import dev.lumentae.lattice.packet.ServerboundAcceptedRulesPacket;
import dev.lumentae.lattice.util.PacketUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BookViewScreen.class)
public class BookViewScreenMixin {
    @Inject(method = "forcePage", at = @At("HEAD"))
    public void lattice$forcePage(int page, CallbackInfoReturnable<Boolean> cir) {
        if (page <= 5000) {
            PacketUtils.sendToServer(ServerboundAcceptedRulesPacket.create(false));
        } else if (page > 5000) {
            PacketUtils.sendToServer(ServerboundAcceptedRulesPacket.create(true));
        }
        Mod.viewingRules = false;
        Minecraft.getInstance().setScreenAndShow(null);
    }
}
