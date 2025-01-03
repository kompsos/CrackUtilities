package org.e11eman.crackutilities.network;

import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.ClientLoginPacketListener;
import net.minecraft.network.packet.c2s.login.LoginQueryResponseC2SPacket;
import net.minecraft.network.packet.s2c.login.*;
import net.minecraft.text.Text;
import org.e11eman.crackutilities.utilities.systems.EventSystem;

public class ClientLoginNetworkHandler implements ClientLoginPacketListener {
    public Client bot;
    public EventSystem events;

    public ClientLoginNetworkHandler(Client bot, EventSystem events) {
        this.bot = bot;
        this.events = events;
    }


    @Override
    public void onSuccess(LoginSuccessS2CPacket packet) {
        System.out.println("Received Login Success");
        bot.connection.setState(NetworkState.PLAY);
        bot.playNetworkHandler = new ClientPlayNetworkHandler(bot, events);
        bot.connection.setPacketListener(bot.playNetworkHandler);

        events.invoke("join", packet);
    }

    @Override
    public void onCompression(LoginCompressionS2CPacket packet) {
        System.out.println("Received Compression");
        if (!bot.connection.isLocal()) {
            bot.connection.setCompressionThreshold(packet.getCompressionThreshold(), false);
        }
    }

    @Override
    public void onQueryRequest(LoginQueryRequestS2CPacket packet) {
        System.out.println("There was a plugin request during a bot login for some reason.");
        bot.connection.send(new LoginQueryResponseC2SPacket(packet.getQueryId(),  null));
    }

    @Override
    public void onDisconnect(LoginDisconnectS2CPacket packet) {
        bot.connection.disconnect(packet.getReason());
        bot.disconnected = true;
        System.out.println(" disconnected: " + packet.getReason().getString());

        events.invoke("disconnectPacket", packet);
    }

    @Override
    public void onDisconnected(Text reason) {
        bot.disconnected = true;
        System.out.println("Bot disconnected: " + reason.getString());

        events.invoke("disconnectGeneric", reason);
    }

    @Override
    public boolean isConnectionOpen() {
        return false;
    }

    @Override
    public void onHello(LoginHelloS2CPacket packet) {
        System.out.println("Bot received onHello! This shouldn't happen on a cracked server.");
    }
}
