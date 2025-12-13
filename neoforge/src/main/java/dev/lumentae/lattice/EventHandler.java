package dev.lumentae.lattice;

import dev.lumentae.lattice.packet.ClientboundConfigurationPacket;
import dev.lumentae.lattice.packet.ServerboundAcceptedRulesPacket;
import dev.lumentae.lattice.packet.ServerboundModSharePacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        Event.OnServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        Event.OnServerStopping(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Event.OnRespawn((ServerPlayer) event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        Event.OnJoin(((ServerPlayer) event.getEntity()).connection);
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        Event.OnCommandRegister(event.getDispatcher());
    }

    public static void handleDataForModShare(final ServerboundModSharePacket data, final IPayloadContext context) {
        context.enqueueWork(() -> Event.OnModSharePacket(data));
    }

    public static void handleDataForAcceptedRules(final ServerboundAcceptedRulesPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> Event.OnAcceptedRulesPacket(data, (ServerPlayer) context.player()));
    }

    public static void handleDataForRules(final ClientboundConfigurationPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> ClientEvent.OnConfigurationPacket(data));
    }

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        if (Config.INSTANCE.vanillaMode && FMLLoader.getCurrent().getDist().isDedicatedServer()) return;

        final PayloadRegistrar registrar = event.registrar("1");
        registrar.commonToServer(
                ServerboundModSharePacket.TYPE,
                ServerboundModSharePacket.STREAM_CODEC,
                EventHandler::handleDataForModShare
        );
        registrar.commonToServer(
                ServerboundAcceptedRulesPacket.TYPE,
                ServerboundAcceptedRulesPacket.STREAM_CODEC,
                EventHandler::handleDataForAcceptedRules
        );
        registrar.commonToClient(
                ClientboundConfigurationPacket.TYPE,
                ClientboundConfigurationPacket.STREAM_CODEC,
                EventHandler::handleDataForRules
        );
    }
}