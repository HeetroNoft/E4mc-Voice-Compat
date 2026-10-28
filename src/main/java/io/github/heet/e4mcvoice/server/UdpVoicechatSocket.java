package io.github.heet.e4mcvoice.server;

import de.maxhenkel.voicechat.api.RawUdpPacket;
import de.maxhenkel.voicechat.api.VoicechatSocket;
import io.github.heet.e4mcvoice.E4mcVoiceCompat;
import io.github.heet.e4mcvoice.TunnelPacket;

import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.SocketAddress;

/**
 * Plain UDP voice socket, used when no other plugin provides one.
 * Simple Voice Chat only creates its own default socket when no plugin sets an implementation,
 * so it isn't available to wrap; this mirrors its behavior.
 */
public class UdpVoicechatSocket implements VoicechatSocket {

    private final byte[] buffer = new byte[4096];
    private DatagramSocket socket;

    @Override
    public void open(int port, String bindAddress) throws Exception {
        if (socket != null) {
            throw new IllegalStateException("Socket already opened");
        }
        InetAddress address = null;
        if (!bindAddress.isEmpty()) {
            try {
                address = InetAddress.getByName(bindAddress);
            } catch (Exception e) {
                E4mcVoiceCompat.LOGGER.error("Failed to parse bind IP address '{}'", bindAddress, e);
            }
        }
        try {
            socket = new DatagramSocket(port, address);
        } catch (BindException e) {
            if (address == null) {
                throw e;
            }
            E4mcVoiceCompat.LOGGER.error("Failed to bind to address '{}', binding to wildcard IP instead", bindAddress);
            socket = new DatagramSocket(port);
        }
    }

    @Override
    public RawUdpPacket read() throws Exception {
        if (socket == null) {
            throw new IllegalStateException("Socket not opened yet");
        }
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        if (packet.getLength() >= buffer.length) {
            throw new ProtocolException("Packet from %s is too large".formatted(packet.getSocketAddress()));
        }
        long timestamp = System.currentTimeMillis();
        byte[] data = new byte[packet.getLength()];
        System.arraycopy(packet.getData(), packet.getOffset(), data, 0, packet.getLength());
        return new TunnelPacket(data, timestamp, packet.getSocketAddress());
    }

    @Override
    public void send(byte[] data, SocketAddress address) throws Exception {
        if (socket == null || socket.isClosed()) {
            return;
        }
        socket.send(new DatagramPacket(data, data.length, address));
    }

    @Override
    public int getLocalPort() {
        return socket == null ? -1 : socket.getLocalPort();
    }

    @Override
    public void close() {
        if (socket != null) {
            try {
                socket.close();
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public boolean isClosed() {
        return socket == null || socket.isClosed();
    }
}
