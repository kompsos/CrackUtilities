package org.e11eman.crackutilities.utilities.systems;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.MathExtras;
import org.e11eman.crackutilities.wrappers.Player;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

public class CommandCoreSystem {
    private final Vector3d core = new Vector3d(0, 0, 0);
    private final ArrayList<BlockPos> cantuseBlocks = new ArrayList<>();
    public Vector3d nextBlock = new Vector3d(0, 0, 0);
    private Vec3d playerPos;
    private Vec3d lastUpdate = new Vec3d(0, 0, 0);

    public CommandCoreSystem() {
        CClient.events.register("serverGameMessage", "commandCoreGameMessage", (Event) -> {
            GameMessageS2CPacket packet = (GameMessageS2CPacket) Event[0];
            CallbackInfo callbackInfo = (CallbackInfo) Event[1];

            if (packet.content().getString().startsWith("Command set:")) callbackInfo.cancel();
        });

        CClient.events.register("playerMovement", "commandCorePlayerMovement", (Event) -> {
            boolean enabled = CClient.configSystem.getValue("commandCoreSystem", "enabled").getAsBoolean();
            if (enabled) {
                playerPos = (Vec3d) Event[0];
                double distanceBetweenLastUpdateAndPlayer = MathExtras.calculateDistanceBetweenPoints(playerPos.x, playerPos.z, lastUpdate.x, lastUpdate.z);

                assert MinecraftClient.getInstance().world != null;

                int simDistance = MinecraftClient.getInstance().world.getSimulationDistance();

                if (distanceBetweenLastUpdateAndPlayer > simDistance * 16) {
                    CClient.scheduler.submit(() -> {
                        update();
                        fillCore();
                    });
                }
            }
        });

        ClientChunkEvents.CHUNK_LOAD.register((clientWorld, chunk) -> {
            if (
                    chunk.getPos().getStartX() == Math.round(core.x) && chunk.getPos().getStartZ() == Math.round(core.z) &&
                            chunk.getPos().getEndX() == (core.x + 15) && chunk.getPos().getEndZ() == (core.z + 15)
            ) {
                CClient.scheduler.submit(this::fillCore);
            }
        });

        CClient.events.register("vanillaBlockBreak", "commandCoreBlockUpdate", (Event) -> {
            double layers = CClient.configSystem.getValue("commandCoreSystem", "layers").getAsDouble();
            BlockPos blockPos = (BlockPos) Event[0];
            BlockState newBlock = (BlockState) Event[2];

            WorldChunk chunk = Player.getPlayer().getWorld().getWorldChunk(blockPos);

            if (
                    chunk.getPos().getStartX() == Math.round(core.x) && chunk.getPos().getStartZ() == Math.round(core.z) &&
                            chunk.getPos().getEndX() == (core.x + 15) && chunk.getPos().getEndZ() == (core.z + 15) &&
                            blockPos.getY() < layers &&
                            blockPos.getY() > -1 &&
                            !(newBlock.getBlock().equals(Blocks.REPEATING_COMMAND_BLOCK) || newBlock.getBlock().equals(Blocks.COMMAND_BLOCK))
            ) {
                cantuseBlocks.add(blockPos);

                CClient.scheduler.submit(() -> run(String.format("setblock %s %s %s minecraft:command_block", blockPos.getX(), blockPos.getY(), blockPos.getZ())));
            }

            if (
                    chunk.getPos().getStartX() == Math.round(core.x) && chunk.getPos().getStartZ() == Math.round(core.z) &&
                            chunk.getPos().getEndX() == (core.x + 15) && chunk.getPos().getEndZ() == (core.z + 15) &&
                            blockPos.getY() < layers &&
                            blockPos.getY() > -1 &&
                            (newBlock.getBlock().equals(Blocks.REPEATING_COMMAND_BLOCK) || newBlock.getBlock().equals(Blocks.COMMAND_BLOCK))
            ) {
                cantuseBlocks.remove(blockPos);
            }
        });
    }

    public void update() {
        core.set(Math.floor(playerPos.x / 16) * 16, 0, Math.floor(playerPos.z / 16) * 16);
        lastUpdate = playerPos;
    }

    public void fillCore() {
        boolean enabled = CClient.configSystem.getValue("commandCoreSystem", "enabled").getAsBoolean();
        double layers = CClient.configSystem.getValue("commandCoreSystem", "layers").getAsDouble();
        if (!enabled) return;
        CClient.chatQueueSystem.addMessageToQueue("/fill " + Math.round(core.x) + " " + 0 + " " + Math.round(core.z) + " " + Math.round(core.x + 15) + " " + Math.round(layers - 1) + " " + Math.round(core.z + 15) + " command_block{CustomName:'[{\"text\":\"CrackUtilitiesCore\",\"bold\":true,\"color\":\"blue\"}]'} replace");
    }

    public void run(String command) {
        boolean enabled = CClient.configSystem.getValue("commandCoreSystem", "enabled").getAsBoolean();
        double layers = CClient.configSystem.getValue("commandCoreSystem", "layers").getAsDouble();
        if (!enabled) return;

        nextBlock.x++;

        if (nextBlock.x >= 16) {
            nextBlock.x = 0;
            nextBlock.y++;
        }

        if (nextBlock.y >= Math.round(layers)) {
            nextBlock.y = 0;
            nextBlock.z++;
        }

        if (nextBlock.z >= 16) {
            nextBlock.z = 0;
        }

        if (cantuseBlocks.contains(new BlockPos((int) Math.floor(core.x + nextBlock.x), (int) nextBlock.y, (int) Math.floor(core.z + nextBlock.z)))) {
            fillCore();
        }


        UpdateCommandBlockC2SPacket packet = new UpdateCommandBlockC2SPacket(new BlockPos((int) Math.floor(core.x + nextBlock.x), (int) nextBlock.y, (int) Math.floor(core.z + nextBlock.z)), command, CommandBlockBlockEntity.Type.AUTO, false, false, true);
        Player.sendPacket(packet);
    }
}