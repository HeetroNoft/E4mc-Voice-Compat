package io.github.heet.e4mcvoice;

import io.github.heet.e4mcvoice.network.TunnelEnablePayload;
import io.github.heet.e4mcvoice.network.VoicePayload;
import io.github.heet.e4mcvoice.server.ServerTunnel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class E4mcVoiceCompat implements ModInitializer {

    public static final String MOD_ID = "e4mc_voice_compat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.clientboundPlay().register(TunnelEnablePayload.TYPE, TunnelEnablePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(VoicePayload.TYPE, VoicePayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(VoicePayload.TYPE, VoicePayload.CODEC);

        // Voice payloads are consumed on the network thread by ServerGamePacketListenerImplMixin before
        // they reach this receiver; it only exists so the channel is advertised to clients.
        ServerPlayNetworking.registerGlobalReceiver(VoicePayload.TYPE, (payload, context) -> {
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerTunnel.onJoin(handler, sender));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ServerTunnel.onDisconnect(handler));
    }
}
