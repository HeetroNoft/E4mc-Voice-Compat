package io.github.heet.e4mcvoice.network;

import io.github.heet.e4mcvoice.E4mcVoiceCompat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sent by the server to a client connected through e4mc: from now on, voice packets
 * must go through the Minecraft connection instead of UDP.
 */
public record TunnelEnablePayload() implements CustomPacketPayload {

    public static final TunnelEnablePayload INSTANCE = new TunnelEnablePayload();

    public static final Type<TunnelEnablePayload> TYPE = new Type<>(E4mcVoiceCompat.id("enable"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TunnelEnablePayload> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
