package dev.lumentae.lattice.packet;

import dev.lumentae.lattice.Constants;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ServerboundAcceptedRulesPacket(boolean accepted) implements CustomPacketPayload {
    public static final Type<ServerboundAcceptedRulesPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "accepted_rules"));
    public static final StreamCodec<FriendlyByteBuf, ServerboundAcceptedRulesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, ServerboundAcceptedRulesPacket::accepted,
            ServerboundAcceptedRulesPacket::new
    );

    public static ServerboundAcceptedRulesPacket create(boolean accepted) {
        return new ServerboundAcceptedRulesPacket(accepted);
    }

    @NotNull
    @Override
    public CustomPacketPayload.Type<ServerboundAcceptedRulesPacket> type() {
        return TYPE;
    }
}