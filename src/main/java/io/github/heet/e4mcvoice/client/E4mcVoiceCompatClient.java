package io.github.heet.e4mcvoice.client;

import io.github.heet.e4mcvoice.network.TunnelEnablePayload;
import io.github.heet.e4mcvoice.network.VoicePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class E4mcVoiceCompatClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Registering this receiver is also what tells the server this client has the mod
        ClientPlayNetworking.registerGlobalReceiver(TunnelEnablePayload.TYPE, (payload, context) ->
                ClientTunnel.enable(context.player().connection.getConnection()));

        // Voice payloads are consumed on the network thread by ClientCommonPacketListenerImplMixin before
        // they reach this receiver; it only exists so the channel is advertised to the server.
        ClientPlayNetworking.registerGlobalReceiver(VoicePayload.TYPE, (payload, context) -> {
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> ClientTunnel.reset());
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientTunnel.reset());
    }
}
