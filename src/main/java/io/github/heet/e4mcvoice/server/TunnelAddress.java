package io.github.heet.e4mcvoice.server;

import java.net.SocketAddress;
import java.util.UUID;

/**
 * Virtual address identifying a player whose voice traffic is tunneled.
 * Simple Voice Chat matches clients by {@link SocketAddress#equals}, so this only needs value semantics.
 */
public final class TunnelAddress extends SocketAddress {

    private final UUID playerId;

    public TunnelAddress(UUID playerId) {
        this.playerId = playerId;
    }

    public UUID playerId() {
        return playerId;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TunnelAddress other && playerId.equals(other.playerId);
    }

    @Override
    public int hashCode() {
        return playerId.hashCode();
    }

    @Override
    public String toString() {
        return "e4mc-tunnel/" + playerId;
    }
}
