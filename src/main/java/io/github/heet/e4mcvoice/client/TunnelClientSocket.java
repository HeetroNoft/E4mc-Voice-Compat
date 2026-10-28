package io.github.heet.e4mcvoice.client;

import de.maxhenkel.voicechat.api.ClientVoicechatSocket;
import de.maxhenkel.voicechat.api.RawUdpPacket;
import io.github.heet.e4mcvoice.E4mcVoiceCompat;
import io.github.heet.e4mcvoice.TunnelPacket;
import io.github.heet.e4mcvoice.network.VoicePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.Connection;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Replaces Simple Voice Chat's client UDP socket: everything goes through the Minecraft connection.
 */
public class TunnelClientSocket implements ClientVoicechatSocket {

    private static final int QUEUE_CAPACITY = 1024;
    // Only used for Simple Voice Chat's log messages
    private static final SocketAddress SERVER_ADDRESS = InetSocketAddress.createUnresolved("e4mc-tunnel", 0);

    private final BlockingQueue<RawUdpPacket> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private volatile boolean closed;

    @Override
    public void open() {
    }

    void receive(byte[] data) {
        if (closed) {
            return;
        }
        // Voice is real-time: when the client can't keep up, dropping is better than buffering
        if (!queue.offer(new TunnelPacket(data, System.currentTimeMillis(), SERVER_ADDRESS))) {
            E4mcVoiceCompat.LOGGER.debug("Voice packet queue full, dropping packet");
        }
    }

    @Override
    public RawUdpPacket read() throws Exception {
        while (!closed) {
            RawUdpPacket packet = queue.poll(250, TimeUnit.MILLISECONDS);
            if (packet != null) {
                return packet;
            }
        }
        throw new SocketException("Socket closed");
    }

    @Override
    public void send(byte[] data, SocketAddress address) throws Exception {
        Connection connection = ClientTunnel.connection();
        if (closed || connection == null || !connection.isConnected()) {
            return;
        }
        connection.send(ClientPlayNetworking.createServerboundPacket(new VoicePayload(data)));
    }

    @Override
    public void close() {
        closed = true;
        queue.clear();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
