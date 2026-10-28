package io.github.heet.e4mcvoice.client;

import io.github.heet.e4mcvoice.network.VoicePayload;
import net.minecraft.network.Connection;
import org.jspecify.annotations.Nullable;

/**
 * Client-side state: whether the current server asked for voice tunneling, and the active tunnel socket.
 */
public final class ClientTunnel {

    @Nullable
    private static volatile Connection connection;
    @Nullable
    private static volatile TunnelClientSocket socket;

    private ClientTunnel() {
    }

    public static boolean isActive() {
        return connection != null;
    }

    static void enable(Connection serverConnection) {
        connection = serverConnection;
    }

    static void reset() {
        connection = null;
        TunnelClientSocket current = socket;
        socket = null;
        if (current != null) {
            current.close();
        }
    }

    public static TunnelClientSocket openSocket() {
        TunnelClientSocket newSocket = new TunnelClientSocket();
        socket = newSocket;
        return newSocket;
    }

    /**
     * Called on the network thread when a tunneled voice packet arrives from the server.
     */
    public static void receive(VoicePayload payload) {
        TunnelClientSocket current = socket;
        if (current != null) {
            current.receive(payload.data());
        }
    }

    @Nullable
    static Connection connection() {
        return connection;
    }
}
