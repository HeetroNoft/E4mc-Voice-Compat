package io.github.heet.e4mcvoice;

import de.maxhenkel.voicechat.api.RawUdpPacket;

import java.net.SocketAddress;

public record TunnelPacket(byte[] data, long timestamp, SocketAddress address) implements RawUdpPacket {

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public SocketAddress getSocketAddress() {
        return address;
    }
}
