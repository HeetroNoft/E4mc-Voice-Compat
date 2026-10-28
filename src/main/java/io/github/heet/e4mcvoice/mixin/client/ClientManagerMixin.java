package io.github.heet.e4mcvoice.mixin.client;

import io.github.heet.e4mcvoice.client.ClientTunnel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.SocketAddress;

/**
 * Simple Voice Chat derives the UDP server IP from the Minecraft connection's remote address and
 * rejects anything that isn't an InetSocketAddress, such as e4mc's Dialtone address. The IP is
 * unused when tunneling, so any resolvable value lets the connection proceed.
 */
@Mixin(targets = "de.maxhenkel.voicechat.voice.client.ClientManager")
public abstract class ClientManagerMixin {

    @Inject(method = "resolveAddress", at = @At("HEAD"), cancellable = true)
    private static void e4mcvoice$skipResolveWhenTunneled(SocketAddress socketAddress, CallbackInfoReturnable<String> cir) {
        if (ClientTunnel.isActive()) {
            cir.setReturnValue("127.0.0.1");
        }
    }
}
