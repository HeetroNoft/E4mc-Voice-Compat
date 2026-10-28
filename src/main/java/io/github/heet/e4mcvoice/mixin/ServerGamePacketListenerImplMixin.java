package io.github.heet.e4mcvoice.mixin;

import io.github.heet.e4mcvoice.network.VoicePayload;
import io.github.heet.e4mcvoice.server.ServerTunnel;
import net.minecraft.network.protocol.common.ServerboundCustomPayloadPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles voice payloads directly on the network thread. Fabric API's receivers run on the
 * server thread, which would add up to a tick of latency and stall voice during lag spikes.
 * Priority is below Fabric's (999) so this runs before Fabric dispatches the payload.
 */
@Mixin(value = ServerGamePacketListenerImpl.class, priority = 900)
public abstract class ServerGamePacketListenerImplMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void e4mcvoice$handleVoicePayload(ServerboundCustomPayloadPacket packet, CallbackInfo ci) {
        if (packet.payload() instanceof VoicePayload payload) {
            ServerTunnel.receive((ServerGamePacketListenerImpl) (Object) this, payload);
            ci.cancel();
        }
    }
}
