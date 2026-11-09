package io.github.heet.e4mcvoice.mixin.client;

import io.github.heet.e4mcvoice.client.ClientTunnel;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Simple Voice Chat derives the UDP server IP from the Minecraft connection's remote address and
 * rejects anything that isn't an InetSocketAddress, such as e4mc's Dialtone address. The IP is
 * unused when tunneling, so any address lets the connection proceed.
 * In Simple Voice Chat 2.5.12 (Minecraft 1.20.5), the remote address comes from this method:
 * later versions resolve it in ClientManager.resolveAddress instead.
 */
@Mixin(targets = "de.maxhenkel.voicechat.intercompatibility.FabricClientCompatibilityManager", remap = false)
public abstract class FabricClientCompatibilityManagerMixin {

    @Inject(method = "getSocketAddress", at = @At("HEAD"), cancellable = true)
    private void e4mcvoice$skipResolveWhenTunneled(Connection connection, CallbackInfoReturnable<SocketAddress> cir) {
        if (ClientTunnel.isActive()) {
            cir.setReturnValue(InetSocketAddress.createUnresolved("127.0.0.1", 0));
        }
    }
}
