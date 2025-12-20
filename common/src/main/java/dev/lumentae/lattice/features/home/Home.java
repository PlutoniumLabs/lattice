package dev.lumentae.lattice.features.home;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record Home(String name, UUID ownerUUID, ResourceLocation dimension, double x, double y, double z, float yaw,
                   float pitch) {
    public static Home create(String name, Player player) {
        return new Home(
                name,
                player.getUUID(),
                player.level().dimension().location(),
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getYRot(),
                player.getXRot()
        );
    }
}
