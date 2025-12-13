package dev.lumentae.lattice;

import dev.lumentae.lattice.packet.ClientboundConfigurationPacket;
import dev.lumentae.lattice.packet.ServerboundAcceptedRulesPacket;
import dev.lumentae.lattice.packet.ServerboundModSharePacket;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;

public class Lattice implements ModInitializer {
    @Override
    public void onInitialize() {
        Mod.init();

        if (!Config.INSTANCE.vanillaMode && FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) {
            PayloadTypeRegistry.playC2S().register(ServerboundModSharePacket.TYPE, ServerboundModSharePacket.STREAM_CODEC);
            PayloadTypeRegistry.playC2S().register(ServerboundAcceptedRulesPacket.TYPE, ServerboundAcceptedRulesPacket.STREAM_CODEC);
            PayloadTypeRegistry.playS2C().register(ClientboundConfigurationPacket.TYPE, ClientboundConfigurationPacket.STREAM_CODEC);
            ServerPlayNetworking.registerGlobalReceiver(ServerboundModSharePacket.TYPE, (payload, context) -> Event.OnModSharePacket(payload));
            ServerPlayNetworking.registerGlobalReceiver(ServerboundAcceptedRulesPacket.TYPE, (payload, context) -> Event.OnAcceptedRulesPacket(payload, context.player()));
        }

        ServerLifecycleEvents.SERVER_STARTED.register(Event::OnServerStarted);
        ServerLifecycleEvents.SERVER_STOPPING.register(Event::OnServerStopping);
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            Event.OnRespawn(newPlayer);
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            Event.OnJoin(handler);
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            Event.OnCommandRegister(dispatcher);
        });
    }
}
