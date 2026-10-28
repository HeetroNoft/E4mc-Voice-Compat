package io.github.heet.e4mcvoice.server;

import io.github.heet.e4mcvoice.E4mcVoiceCompat;
import io.github.heet.e4mcvoice.mixin.ServerCommonPacketListenerImplAccessor;
import io.github.heet.e4mcvoice.network.TunnelEnablePayload;
import io.github.heet.e4mcvoice.network.VoicePayload;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Server-side state: which players have their voice traffic tunneled, and the active voice socket.
 */
public final class ServerTunnel {

    private static final Map<UUID, Connection> TUNNELED = new ConcurrentHashMap<>();

    @Nullable
    private static volatile TunnelServerSocket socket;

    private ServerTunnel() {
    }

    public static void setSocket(@Nullable TunnelServerSocket newSocket) {
        socket = newSocket;
    }

    public static void onJoin(ServerGamePacketListenerImpl handler, PacketSender sender) {
        Connection connection = ((ServerCommonPacketListenerImplAccessor) handler).e4mcvoice$getConnection();
        if (!E4mcDetector.isE4mcConnection(connection)) {
            return;
        }
        String name = handler.player.getName().getString();
        String via = E4mcDetector.describe(connection);
        if (ServerPlayNetworking.canSend(handler, TunnelEnablePayload.TYPE)) {
            TUNNELED.put(handler.player.getUUID(), connection);
            // Sent before Simple Voice Chat's secret, which is only sent once the client asks for it
            sender.sendPacket(TunnelEnablePayload.INSTANCE);
            E4mcVoiceCompat.LOGGER.info("Tunneling voice chat of {} through the Minecraft connection (e4mc {})", name, via);
        } else {
            E4mcVoiceCompat.LOGGER.warn("{} joined through e4mc ({}) without e4mc Voice Compat, voice chat will not work for them", name, via);
            handler.player.sendSystemMessage(Component.literal(
                    "Voice chat won't work for you on this world: you joined through e4mc without the \"e4mc Voice Compat\" mod. Install it to use voice chat."
            ).withStyle(ChatFormatting.YELLOW));
        }
    }

    public static void onDisconnect(ServerGamePacketListenerImpl handler) {
        TUNNELED.remove(handler.player.getUUID());
    }

    /**
     * Called on the network thread when a tunneled voice packet arrives from a client.
     */
    public static void receive(ServerGamePacketListenerImpl handler, VoicePayload payload) {
        UUID playerId = handler.player.getUUID();
        TunnelServerSocket current = socket;
        if (current == null || !TUNNELED.containsKey(playerId)) {
            return;
        }
        current.receive(payload.data(), new TunnelAddress(playerId));
    }

    static void send(UUID playerId, byte[] data) {
        Connection connection = TUNNELED.get(playerId);
        if (connection == null || !connection.isConnected()) {
            return;
        }
        connection.send(ServerPlayNetworking.createClientboundPacket(new VoicePayload(data)));
    }
}
