package org.e11eman.crackutilities.utilities.systems;

import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.e11eman.crackutilities.network.Client;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.MessagePresets;
import org.e11eman.crackutilities.utilities.toolclasses.RaycastUtilities;
import org.e11eman.crackutilities.wrappers.Player;

import java.time.Instant;
import java.util.ArrayList;

public class BotManagerSystem {
    public boolean clientSync = false;
    public String shape = "line";
    public ArrayList<Client> clients = new ArrayList<>();
    public boolean pinPointLook = false;

    public void newBot(String username) {
        Client clientInstance = new Client(username, Player.getNetworkHandler().getServerInfo());

        CClient.scheduler.submit(clientInstance::login);

        clientInstance.events.register("join", "botLogin", (e) -> {
            Vec3d pos = Player.getPosition();

            clients.add(clientInstance);
            CClient.commandCoreSystem.run(String.format("/tp %s %s %s %s", clientInstance.username, pos.x, pos.y, pos.z));
        });
        clientInstance.events.register("disconnectGeneric", "botDisGeneric", (e) -> clients.remove(clientInstance));
    }

    public void endSync() {
        clientSync = false;
        CClient.events.unregisterInstance("c2s", "c2sBotClientSync");
        CClient.events.unregisterInstance("tick", "botSyncTick");
        Player.alertClient(MessagePresets.falseTextPreset("Disable client sync!"));
    }

    public void startSync() {
        clientSync = true;

        moveAllToPlayer();

        CClient.events.register("tick", "botSyncTick", (e) -> {
            switch (shape) {
                case "line" -> syncPosInLine();
                case "circle" -> syncPosInCircle();
            }
        });

        CClient.events.register("c2s", "c2sBotClientSync", (packet) -> {
            Packet<?> interruptedPacket = (Packet<?>) packet[0];

            if(interruptedPacket instanceof ClientCommandC2SPacket) {
                for (Client selectedClient : clients) {
                    selectedClient.sendPacket(new ClientCommandC2SPacket(selectedClient.player, ((ClientCommandC2SPacket) interruptedPacket).getMode()));
                }
            }

            if(
                    interruptedPacket instanceof CreativeInventoryActionC2SPacket ||
                            interruptedPacket instanceof HandSwingC2SPacket ||
                            interruptedPacket instanceof UpdateSelectedSlotC2SPacket ||
                            interruptedPacket instanceof PlayerActionC2SPacket ||
                            interruptedPacket instanceof PlayerInteractItemC2SPacket ||
                            interruptedPacket instanceof UpdatePlayerAbilitiesC2SPacket
            ) {
                sendAllClients(interruptedPacket);
            }
        });

        Player.alertClient(MessagePresets.trueTextPreset("Enabled client sync!"));
    }

    public void syncPosInLine() {
        Vec3d playerPos = Player.getPosition();
        double spread = CClient.configSystem.getValue("botManager", "lineSpread").getAsDouble();

        for (int i = 0; i < clients.size(); i++) {
            double posX = Math.cos(Math.toRadians(Player.getYaw()));
            double posZ = Math.sin(Math.toRadians(Player.getYaw()));
            double distance = i % 2 == 0 ? i + 1 : -i;

            double finalX = playerPos.x + (posX * distance * spread);
            double finalZ = playerPos.z + (posZ * distance * spread);

            Client selectedClient = clients.get(i);

            syncPosGeneral(selectedClient, finalX, playerPos.y, finalZ, Player.getYaw(), Player.getPitch(), Player.isOnGround());
        }
    }

    public void syncPosInCircle() {
        Vec3d pos = Player.getPosition();
        double speed = CClient.configSystem.getValue("botManager", "circleRotateSpeed").getAsDouble();
        double size = CClient.configSystem.getValue("botManager", "circleSize").getAsDouble();

        for (int i = 0; i < clients.size(); i++) {
            double x = pos.x +
                    (Math.sin((((double) Instant.now().toEpochMilli() / (1000 * 30) * speed) + ((double) i / clients.size() - 1)) * Math.PI * 2) * size);

            double z = pos.z +
                    (Math.cos((((double) Instant.now().toEpochMilli() / (1000 * 30) * speed) + ((double) i / clients.size() - 1)) * Math.PI * 2) * size);


            Client selectedClient = clients.get(i);

            syncPosGeneral(selectedClient, x, pos.y, z, Player.getYaw(), Player.getPitch(), Player.isOnGround());
        }
    }

    public void syncPosGeneral(Client client, double x, double y, double z, float yaw, float pitch, boolean onGround) {
        if(pinPointLook) {
            PlayerMoveC2SPacket movePacket = new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, onGround);
            client.sendPacket(movePacket);

            client.player.setPosition(x, y, z);

            Vec3d hit = RaycastUtilities.getLookPos(200);

            Vec3d eyePos = client.player.getEyePos();

            double d = hit.x - eyePos.x;
            double e = hit.y - eyePos.y;
            double f = hit.z - eyePos.z;

            double g = Math.sqrt(d * d + f * f);

            float pitchF = (MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875))));
            float yawF = (MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0F));

            client.player.setYaw(yawF);
            client.player.setPitch(pitchF);

            PlayerMoveC2SPacket lookPacket = new PlayerMoveC2SPacket.LookAndOnGround(yawF, pitchF, onGround);
            client.sendPacket(lookPacket);
            return;
        }

        PlayerMoveC2SPacket movePacket = new PlayerMoveC2SPacket.Full(x, y, z, yaw, pitch, onGround);
        client.sendPacket(movePacket);
    }

    public void moveAllToPlayer() {
        for (Client selectedClient : clients) {
            selectedClient.sendChat("/tp " + Player.getUsername());
        }
    }

    public <T extends PacketListener> void sendAllClients(Packet<T> packet) {
        for (Client selectClient : clients) {
            selectClient.sendPacket(packet);
        }
    }
}
