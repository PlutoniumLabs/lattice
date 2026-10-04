package dev.lumentae.lattice.packet;

import dev.lumentae.lattice.Constants;
import dev.lumentae.lattice.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record ServerboundModSharePacket(String mods, String resourcePacks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ServerboundModSharePacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "mod_share"));
    public static final StreamCodec<FriendlyByteBuf, ServerboundModSharePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ServerboundModSharePacket::mods,
            ByteBufCodecs.STRING_UTF8, ServerboundModSharePacket::resourcePacks,
            ServerboundModSharePacket::new
    );

    @NotNull
    @Override
    public CustomPacketPayload.Type<ServerboundModSharePacket> type() {
        return TYPE;
    }

    public static ServerboundModSharePacket create() {
        String modList = String.join("\n", Services.PLATFORM.getModList());
        String resourcePackList = String.join("\n", Minecraft.getInstance().getResourcePackRepository().getSelectedIds());

        return new ServerboundModSharePacket(modList, resourcePackList);
    }
}
