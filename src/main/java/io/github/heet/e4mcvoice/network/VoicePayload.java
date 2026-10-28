package io.github.heet.e4mcvoice.network;

import io.github.heet.e4mcvoice.E4mcVoiceCompat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * One raw Simple Voice Chat UDP datagram, carried inside the Minecraft connection.
 * The data is already encrypted by Simple Voice Chat, so it is forwarded as-is.
 */
public record VoicePayload(byte[] data) implements CustomPacketPayload {

    /**
     * Same limit as Simple Voice Chat's UDP receive buffer.
     */
    public static final int MAX_SIZE = 4096;

    public static final Type<VoicePayload> TYPE = new Type<>(E4mcVoiceCompat.id("voice"));

    public static final StreamCodec<RegistryFriendlyByteBuf, VoicePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.byteArray(MAX_SIZE), VoicePayload::data,
            VoicePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
