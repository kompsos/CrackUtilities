package org.e11eman.crackutilities.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.Address;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkState;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.message.ArgumentSignatureDataMap;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;
import org.e11eman.crackutilities.utilities.systems.EventSystem;

import java.net.InetSocketAddress;
import java.time.Instant;
import java.util.BitSet;
import java.util.Optional;

public class Client {
    public ClientConnection connection;
    public boolean loggingIn = false;
    public boolean disconnected = false;
    public ClientPlayNetworkHandler playNetworkHandler;
    public String username;
    public ServerInfo serverInfo;
    public EventSystem events = new EventSystem();
    public int entityId;
    public ClientPlayerEntity player;

    public Client(String username, ServerInfo serverInfo) {
        this.username = username;
        this.serverInfo = serverInfo;
    }

    public void login() {
        if (connection != null && connection.isOpen()) {
            throw new RuntimeException("Old connection is still open");
        }

        ServerAddress address = ServerAddress.parse(serverInfo.address);
        Optional<InetSocketAddress> optional = AllowedAddressResolver.DEFAULT.resolve(address).map(Address::getInetSocketAddress);

        if (optional.isEmpty()) {
            throw new RuntimeException("Bot with name " + username + " failed to resolve its target server address");
        }
        InetSocketAddress inetSocketAddress = optional.get();

        connection = ClientConnection.connect(inetSocketAddress, MinecraftClient.getInstance().options.useNativeTransport);

        loggingIn = true;
        connection.setPacketListener(new ClientLoginNetworkHandler(this, events));
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        connection.send(new HandshakeC2SPacket(inetSocketAddress.getHostName(), inetSocketAddress.getPort(), NetworkState.LOGIN));
        connection.send(new LoginHelloC2SPacket(username, Optional.empty()));
    }

    public void disconnect() {
        this.connection.disconnect(Text.empty());
        this.disconnected = true;
    }

    public <T extends PacketListener> void sendPacket(Packet<T> packet) {
        this.connection.send(packet);
    }

    public void sendChat(String chat) {
        if (chat.startsWith("/")) {
            sendPacket(new CommandExecutionC2SPacket(chat.substring(1), Instant.now(), 0, ArgumentSignatureDataMap.EMPTY, new LastSeenMessageList.Acknowledgment(0, new BitSet())));
        } else {
            sendPacket(new ChatMessageC2SPacket(chat, Instant.now(), 0, null, new LastSeenMessageList.Acknowledgment(0, new BitSet())));
        }
    }
}
