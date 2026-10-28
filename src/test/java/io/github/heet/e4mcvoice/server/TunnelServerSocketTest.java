package io.github.heet.e4mcvoice.server;

import de.maxhenkel.voicechat.api.RawUdpPacket;
import org.junit.jupiter.api.Test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TunnelServerSocketTest {

    /**
     * Regression: Simple Voice Chat hands plugins a null socket by default,
     * which used to crash the voice server for every player, host included.
     */
    @Test
    void directPlayersStillUseUdp() throws Exception {
        TunnelServerSocket server = new TunnelServerSocket(new UdpVoicechatSocket());
        server.open(0, "");
        try (DatagramSocket client = new DatagramSocket()) {
            InetAddress loopback = InetAddress.getLoopbackAddress();
            byte[] request = {1, 2, 3};
            client.send(new DatagramPacket(request, request.length, loopback, server.getLocalPort()));

            RawUdpPacket received = server.read();
            assertArrayEquals(request, received.getData());

            byte[] reply = {4, 5};
            server.send(reply, received.getSocketAddress());
            DatagramPacket answer = new DatagramPacket(new byte[16], 16);
            client.setSoTimeout(2000);
            client.receive(answer);
            assertArrayEquals(reply, Arrays.copyOf(answer.getData(), answer.getLength()));
        } finally {
            server.close();
        }
    }

    @Test
    void tunneledPacketsAreMergedWithUdp() throws Exception {
        TunnelServerSocket server = new TunnelServerSocket(new UdpVoicechatSocket());
        server.open(0, "");
        try {
            TunnelAddress address = new TunnelAddress(UUID.randomUUID());
            server.receive(new byte[]{9}, address);

            RawUdpPacket received = server.read();
            assertArrayEquals(new byte[]{9}, received.getData());
            assertEquals(address, received.getSocketAddress());
        } finally {
            server.close();
        }
    }

    @Test
    void closeUnblocksRead() throws Exception {
        TunnelServerSocket server = new TunnelServerSocket(new UdpVoicechatSocket());
        server.open(0, "");
        CompletableFuture<Throwable> read = CompletableFuture.supplyAsync(() -> {
            try {
                server.read();
                return null;
            } catch (Throwable t) {
                return t;
            }
        });
        server.close();
        assertInstanceOf(SocketException.class, read.get(2, TimeUnit.SECONDS));
        assertTrue(server.isClosed());
    }
}
