package io.github.heet.e4mcvoice.mixin.client;

import io.github.heet.e4mcvoice.client.ClientTunnel;
import io.github.heet.e4mcvoice.network.VoicePayload;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles voice payloads directly on the network thread instead of waiting for the render thread.
 * Priority is below Fabric's so this runs before Fabric dispatches the payload.
 */
@Mixin(value = ClientCommonPacketListenerImpl.class, priority = 900)
public abstract class ClientCommonPacketListenerImplMixin {

    @Inject(method = "handleCustomPayload(Lnet/minecraft/network/protocol/common/ClientboundCustomPayloadPacket;)V", at = @At("HEAD"), cancellable = true)
    private void e4mcvoice$handleVoicePayload(ClientboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (packet.payload() instanceof VoicePayload payload) {
            ClientTunnel.receive(payload);
            ci.cancel();
        }
    }
}
