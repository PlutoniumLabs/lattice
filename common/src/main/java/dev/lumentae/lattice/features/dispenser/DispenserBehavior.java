package dev.lumentae.lattice.features.dispenser;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DispenserBehavior {
    @NotNull
    public static DispenseItemBehavior getDispenserBehavior(ServerPlayer player) {
        ServerPlayerGameMode manager = new ServerPlayerGameMode(player);
        manager.changeGameModeForPlayer(GameType.SURVIVAL);
        return (pointer, stack) -> {
            ServerLevel level = pointer.level();
            player.setServerLevel(level);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);

            Direction direction = pointer.state().getValue(DispenserBlock.FACING);
            BlockPos relative = pointer.pos().relative(direction, 1);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, new AABB(relative));
            if ((stack.getItem().getDescriptionId().endsWith("_axe") || stack.getItem().getDescriptionId().endsWith("sword")) && !entities.isEmpty()) {
                for (Entity entity : entities) {
                    int damage = Utils.getDamage(stack);
                    entity.hurt(level.damageSources().generic(), damage);
                }
            } else if (stack.getItem().getDescriptionId().endsWith("axe") || stack.getItem().getDescriptionId().endsWith("shovel")) {
                BlockState toBreak = level.getBlockState(relative);

                // Check if the block can be broken with a pickaxe, axe or shovel
                if (toBreak.is(BlockTags.MINEABLE_WITH_PICKAXE) ||    // Pickaxe
                        toBreak.is(BlockTags.MINEABLE_WITH_AXE) ||    // Axe
                        toBreak.is(BlockTags.MINEABLE_WITH_SHOVEL)) { // Shovel

                    manager.destroyBlock(relative);

                    // Reset durability
                    if (!Config.INSTANCE.dispenserUsesDurability) stack.setDamageValue(stack.getDamageValue() - 1);
                }
            } else if (stack.getItem() instanceof BlockItem) {
                // Place block on top if the front
                Direction direction2 = level.isEmptyBlock(relative) ? direction : Direction.UP;

                try {
                    ((BlockItem) stack.getItem()).place(new DirectionalPlaceContext(level, relative, direction, stack, direction2));
                } catch (Exception ignored) {
                }
            }
            return stack;
        };
    }
}
