package dev.lumentae.lattice.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("RETURN"))
    public void lattice$spawnAtLocation(ServerLevel level, ItemStack itemStack, Vec3 offset, CallbackInfoReturnable<ItemEntity> cir) {
        try {
            if (itemStack.typeHolder().getRegisteredName().equals("minecraft:dragon_egg") && level.dimension().equals(Level.END))
                lattice$setGlowingTag(cir.getReturnValue());
        } catch (Exception ignored) {
        }
    }

    @Unique
    public void lattice$setGlowingTag(Entity entity) {
        entity.setGlowingTag(true);
    }
}
