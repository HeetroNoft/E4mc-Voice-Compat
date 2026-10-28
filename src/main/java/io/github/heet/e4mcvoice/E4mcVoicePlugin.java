package io.github.heet.e4mcvoice;

import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatSocket;
import de.maxhenkel.voicechat.api.events.ClientVoicechatInitializationEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartingEvent;
import io.github.heet.e4mcvoice.client.ClientTunnel;
import io.github.heet.e4mcvoice.server.ServerTunnel;
import io.github.heet.e4mcvoice.server.TunnelServerSocket;
import io.github.heet.e4mcvoice.server.UdpVoicechatSocket;

public class E4mcVoicePlugin implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return E4mcVoiceCompat.MOD_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(VoicechatServerStartingEvent.class, this::onServerStarting);
        registration.registerEvent(ClientVoicechatInitializationEvent.class, this::onClientInitialization);
    }

    private void onServerStarting(VoicechatServerStartingEvent event) {
        // Null unless another plugin already provided a socket: Simple Voice Chat only creates its default one afterwards
        VoicechatSocket udp = event.getSocketImplementation();
        TunnelServerSocket socket = new TunnelServerSocket(udp != null ? udp : new UdpVoicechatSocket());
        ServerTunnel.setSocket(socket);
        event.setSocketImplementation(socket);
    }

    private void onClientInitialization(ClientVoicechatInitializationEvent event) {
        // Only fired on the client, so client classes are safe to load here
        if (ClientTunnel.isActive()) {
            event.setSocketImplementation(ClientTunnel.openSocket());
            E4mcVoiceCompat.LOGGER.info("Voice chat connection tunneled through e4mc");
        }
    }
}
