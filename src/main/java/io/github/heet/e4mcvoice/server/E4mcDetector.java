package io.github.heet.e4mcvoice.server;

import io.github.heet.e4mcvoice.mixin.ConnectionAccessor;
import io.netty.channel.Channel;
import io.netty.incubator.codec.quic.QuicStreamChannel;
import link.e4mc.dialtone.DialtoneChannel;
import net.minecraft.network.Connection;

public final class E4mcDetector {

    private E4mcDetector() {
    }

    /**
     * Server-side check of how a player reached the host.
     * e4mc hands the Minecraft server a QUIC stream for relay (Quiclime) connections,
     * and a {@link DialtoneChannel} for peer-to-peer connections. Direct LAN connections
     * use a regular socket channel and the host's own player uses an in-memory channel.
     */
    public static boolean isE4mcConnection(Connection connection) {
        Channel channel = ((ConnectionAccessor) connection).e4mcvoice$getChannel();
        return channel instanceof QuicStreamChannel || channel instanceof DialtoneChannel;
    }

    public static String describe(Connection connection) {
        Channel channel = ((ConnectionAccessor) connection).e4mcvoice$getChannel();
        if (channel instanceof DialtoneChannel) {
            return "Dialtone";
        }
        if (channel instanceof QuicStreamChannel) {
            return "relay";
        }
        return channel == null ? "unknown" : channel.getClass().getSimpleName();
    }
}
