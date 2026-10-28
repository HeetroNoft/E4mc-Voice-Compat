package io.github.heet.e4mcvoice.server;

import de.maxhenkel.voicechat.api.RawUdpPacket;
import de.maxhenkel.voicechat.api.VoicechatSocket;
import io.github.heet.e4mcvoice.E4mcVoiceCompat;
import io.github.heet.e4mcvoice.TunnelPacket;

import java.net.ProtocolException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Wraps Simple Voice Chat's server socket: players connected directly keep using UDP,
 * players identified by a {@link TunnelAddress} are served through their Minecraft connection.
 * Both sources are merged into a single queue for {@link #read()}.
 */
public class TunnelServerSocket implements VoicechatSocket {

    private static final int QUEUE_CAPACITY = 4096;

    private final VoicechatSocket udp;
    private final BlockingQueue<RawUdpPacket> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private volatile boolean closed;

    public TunnelServerSocket(VoicechatSocket udp) {
        this.udp = udp;
    }

    @Override
    public void open(int port, String bindAddress) throws Exception {
        udp.open(port, bindAddress);
        Thread pump = new Thread(this::pumpUdp, "e4mc-voice-compat-udp");
        pump.setDaemon(true);
        pump.start();
    }

    private void pumpUdp() {
        while (!closed && !udp.isClosed()) {
            try {
                offer(udp.read());
            } catch (ProtocolException e) {
                E4mcVoiceCompat.LOGGER.debug("Skipping invalid UDP voice packet: {}", e.getMessage());
            } catch (Exception e) {
                if (closed || udp.isClosed()) {
                    return;
                }
                E4mcVoiceCompat.LOGGER.debug("Failed to read UDP voice packet", e);
            }
        }
    }

    void receive(byte[] data, TunnelAddress address) {
        if (closed) {
            return;
        }
        offer(new TunnelPacket(data, System.currentTimeMillis(), address));
    }

    private void offer(RawUdpPacket packet) {
        // Voice is real-time: when the server can't keep up, dropping is better than buffering
        if (!queue.offer(packet)) {
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
        if (address instanceof TunnelAddress tunnelAddress) {
            ServerTunnel.send(tunnelAddress.playerId(), data);
        } else {
            udp.send(data, address);
        }
    }

    @Override
    public int getLocalPort() {
        return udp.getLocalPort();
    }

    @Override
    public void close() {
        closed = true;
        udp.close();
        queue.clear();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
