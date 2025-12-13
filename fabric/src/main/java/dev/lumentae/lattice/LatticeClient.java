package dev.lumentae.lattice;

import dev.lumentae.lattice.packet.ClientboundConfigurationPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class LatticeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            Event.OnShareMods(client.player);
        });
        ClientPlayNetworking.registerGlobalReceiver(ClientboundConfigurationPacket.TYPE, (payload, context) -> ClientEvent.OnConfigurationPacket(payload));
        ClientLifecycleEvents.CLIENT_STARTED.register(ClientEvent::OnClientStarted);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> Event.OnClientDisconnect());
    }
}
