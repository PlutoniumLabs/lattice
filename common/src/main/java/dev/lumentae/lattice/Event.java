package dev.lumentae.lattice;

import com.mojang.brigadier.CommandDispatcher;
import dev.lumentae.lattice.command.ICommand;
import dev.lumentae.lattice.features.discord.DiscordRpcManager;
import dev.lumentae.lattice.features.discord.webhook.WebhookMessage;
import dev.lumentae.lattice.features.dispenser.DispenserBehavior;
import dev.lumentae.lattice.packet.ClientboundConfigurationPacket;
import dev.lumentae.lattice.packet.ServerboundAcceptedRulesPacket;
import dev.lumentae.lattice.packet.ServerboundModSharePacket;
import dev.lumentae.lattice.platform.Services;
import dev.lumentae.lattice.util.PacketUtils;
import dev.lumentae.lattice.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.DispenserBlock;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Event {
    public static void OnServerStarted(MinecraftServer server) {
        Mod.setServer(server);
        if (Config.INSTANCE.enableDispenserBehavior)
            return;

        ServerPlayer player = Services.PLATFORM.getFakePlayer(server);
        DispenseItemBehavior behaviors = DispenserBehavior.getDispenserBehavior(player);
        BuiltInRegistries.ITEM.forEach(item -> {
            if (DispenserBlock.DISPENSER_REGISTRY.containsKey(item)) return;
            DispenserBlock.registerBehavior(item, behaviors);
        });
    }

    public static void OnServerStopping(MinecraftServer server) {
        Mod.setServer(null);
        Config.saveConfig();
    }

    public static void OnRespawn(ServerPlayer player) {
        if (player.getLastDeathLocation().isEmpty()) {
            return;
        }
        GlobalPos o = player.getLastDeathLocation().get();
        player.sendSystemMessage(Component.translatable("message.lattice.death.1")
                .append(String.valueOf(o.pos().getX()))
                .append(", ")
                .append(String.valueOf(o.pos().getY()))
                .append(", ")
                .append(String.valueOf(o.pos().getZ()))
                .append(Component.translatable("message.lattice.death.2"))
        );
    }

    public static void OnJoin(ServerGamePacketListenerImpl handler) {
        ServerPlayer player = handler.getPlayer();
        if (player.level().isClientSide()) return;

        Config.INSTANCE.playerOptions.computeIfAbsent(player.getUUID(), k -> Config.DEFAULT_PLAY_OPTIONS);
        if (Config.INSTANCE.serverOpenDate.isAfter(LocalDateTime.now()) && !player.hasPermissions(2)) {
            var reason = Component.translatable("message.lattice.server.closed.1")
                    .append(Component.translatable("message.lattice.server.closed.2"))
                    .append(MutableComponent.create(
                            new PlainTextContents.LiteralContents(Config.INSTANCE.serverOpenDate.toString())
                    ).withStyle(style -> style.withColor(ChatFormatting.GREEN)))
                    .append("!");

            ClientboundDisconnectPacket packet = new ClientboundDisconnectPacket(reason);
            handler.send(packet);
        }

        if (!Config.INSTANCE.vanillaMode)
            PacketUtils.sendToClient(player, ClientboundConfigurationPacket.create(player, Config.INSTANCE.discordRpcConfiguration));
    }

    public static void OnCommandRegister(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (ICommand command : Constants.COMMANDS) {
            command.register(dispatcher);
        }
    }

    public static void OnModSharePacket(ServerboundModSharePacket packet) {
        Constants.LOG.info("Received mod/resource pack list from server:");
        Constants.LOG.info("Origin: {} ({})", packet.origin(), Utils.getPlayerNameByUUID(UUID.fromString(packet.origin())));
        Constants.LOG.info("Mods: {}", packet.mods());
        Constants.LOG.info("Resource Packs: {}", packet.resourcePacks());

        if (Utils.containsIllegalMods(packet.mods()) || Utils.containsIllegalMods(packet.resourcePacks())) {
            Constants.LOG.warn("Illegal mods or resource packs!");

            var illegalMods = new ArrayList<>(packet.mods().lines().filter(Utils::containsIllegalMods).toList());
            illegalMods.addAll(packet.resourcePacks().lines().filter(Utils::containsIllegalMods).toList());

            ServerPlayer player = Utils.getPlayerByUUID(UUID.fromString(packet.origin()));
            if (player == null)
                return;

            Component reason = Component.translatable("message.lattice.illegal_mods").withStyle(ChatFormatting.RED)
                    .append(Component.literal("\n- "))
                    .append(Component.literal(String.join("\n- ", illegalMods)).withStyle(ChatFormatting.RED));

            Constants.LOG.warn("Kicking player {} for illegal mods: {}", player.getName().getString(), String.join(", ", illegalMods));

            player.connection.disconnect(reason);
        }
    }

    public static void OnShareMods(Player player) {
        if (!Config.INSTANCE.vanillaMode)
            PacketUtils.sendToServer(ServerboundModSharePacket.create(player));
    }

    public static void OnAcceptedRulesPacket(ServerboundAcceptedRulesPacket data, ServerPlayer player) {
        if (!data.accepted()) {
            Component reason = Component.translatable("message.lattice.rules.not_accepted").withStyle(ChatFormatting.RED);
            player.connection.disconnect(reason);
            return;
        }

        Config.getPlayerPlayOptions(player.getUUID()).acceptedRules = true;
        Config.saveConfig();
    }

    public static void OnClientDisconnect() {
        DiscordRpcManager.discordRpcConfiguration = Config.INSTANCE.discordRpcConfiguration;
        DiscordRpcManager.updateActivity();
    }

    public static void OnPlayerMessage(PlayerChatMessage message) {
        CompletableFuture.runAsync(() -> {
            String username = Utils.getPlayerByUUID(message.sender()).getName().getString();
            String messageString = message.signedContent();
            String avatarUrl = "https://mc-heads.net/avatar/" + username;

            Mod.webhook.send(new WebhookMessage(username, messageString, avatarUrl));
        });
    }

    public static void OnGameMessage(Component message) {
        CompletableFuture.runAsync(() -> {
            String username = "Server";
            String messageString = message.getString();
            String avatarUrl = "https://minecraft.wiki/images/Java_Edition_icon_3.png";

            Mod.webhook.send(new WebhookMessage(username, messageString, avatarUrl));
        });
    }
}
