package dev.lumentae.lattice.packet;

import dev.lumentae.lattice.Constants;
import dev.lumentae.lattice.features.discord.DiscordRpcConfiguration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ClientboundConfigurationPacket(String discordRpcConfiguration) implements CustomPacketPayload {
    public static final Type<ClientboundConfigurationPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "configuration"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClientboundConfigurationPacket::discordRpcConfiguration,
            ClientboundConfigurationPacket::new
    );

    public static ClientboundConfigurationPacket create(DiscordRpcConfiguration discordRpcConfiguration) {
        return new ClientboundConfigurationPacket(discordRpcConfiguration.toString());
    }

    @NotNull
    @Override
    public CustomPacketPayload.Type<ClientboundConfigurationPacket> type() {
        return TYPE;
    }
}
