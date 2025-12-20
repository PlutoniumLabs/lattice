package dev.lumentae.lattice.packet;

import dev.lumentae.lattice.Config;
import dev.lumentae.lattice.Constants;
import dev.lumentae.lattice.features.discord.DiscordRpcConfiguration;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record ClientboundConfigurationPacket(String rules,
                                             String discordRpcConfiguration) implements CustomPacketPayload {
    public static final Type<ClientboundConfigurationPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "configuration"));
    public static final StreamCodec<FriendlyByteBuf, ClientboundConfigurationPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ClientboundConfigurationPacket::rules,
            ByteBufCodecs.STRING_UTF8, ClientboundConfigurationPacket::discordRpcConfiguration,
            ClientboundConfigurationPacket::new
    );

    public static ClientboundConfigurationPacket create(ServerPlayer player, DiscordRpcConfiguration discordRpcConfiguration) {
        String ruleList = "";
        if (!Config.INSTANCE.rules.isEmpty() && !Config.getPlayerPlayOptions(player.getUUID()).acceptedRules)
            ruleList = String.join("\0", Config.INSTANCE.rules);

        return new ClientboundConfigurationPacket(ruleList, discordRpcConfiguration.toString());
    }

    @NotNull
    @Override
    public CustomPacketPayload.Type<ClientboundConfigurationPacket> type() {
        return TYPE;
    }
}