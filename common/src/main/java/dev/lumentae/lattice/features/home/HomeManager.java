package dev.lumentae.lattice.features.home;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.util.Utils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class HomeManager {
    public static Home getHome(String name, Player owner) {
        for (Home home : Config.getPlayerPlayOptions(owner.getUUID()).homes) {
            if (home.name().equals(name) && home.ownerUUID().equals(owner.getUUID())) {
                return home;
            }
        }
        return null;
    }

    public static List<Home> getHomes(Player owner) {
        return Config.getPlayerPlayOptions(owner.getUUID()).homes;
    }

    public static Home createHome(String name, Player owner) {
        Home home = Home.create(name, owner);
        Config.getPlayerPlayOptions(owner.getUUID()).homes.add(home);
        Config.saveConfig();
        return home;
    }

    public static void deleteHome(Home home, Player owner) {
        if (home == null) {
            return;
        }
        Config.getPlayerPlayOptions(owner.getUUID()).homes.remove(home);
        Config.saveConfig();
    }

    public static void deleteHome(String name, Player owner) {
        Home home = getHome(name, owner);
        if (home == null) return;
        deleteHome(home, owner);
    }

    public static boolean teleportToHome(Home home, Player owner) {
        if (home == null) return false;

        TeleportTransition teleportTransition = new TeleportTransition(
                Utils.getLevelFromIdentifier(home.dimension()),
                new Vec3(home.x(), home.y(), home.z()),
                Vec3.ZERO,
                home.yaw(),
                home.pitch(),
                TeleportTransition.DO_NOTHING
        );
        return owner.teleport(teleportTransition) != null;
    }

    public static boolean teleportToHome(String name, Player owner) {
        Home home = getHome(name, owner);
        return teleportToHome(home, owner);
    }
}
