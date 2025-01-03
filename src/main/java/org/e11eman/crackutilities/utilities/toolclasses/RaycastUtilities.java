package org.e11eman.crackutilities.utilities.toolclasses;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.e11eman.crackutilities.wrappers.Player;

@SuppressWarnings("unused")
public class RaycastUtilities {
    public static Block getBlockLook() {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        assert hit != null;
        if (hit.getType().equals(HitResult.Type.BLOCK)) {
            BlockHitResult blockHit = (BlockHitResult) hit;
            BlockPos blockPos = blockHit.getBlockPos();
            assert client.world != null;
            BlockState blockState = client.world.getBlockState(blockPos);

            return blockState.getBlock();
        } else {
            return null;
        }
    }

    public static BlockPos getBlockPosLook() {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        assert hit != null;
        if (hit.getType().equals(HitResult.Type.BLOCK)) {
            BlockHitResult blockHit = (BlockHitResult) hit;

            return blockHit.getBlockPos();
        } else {
            return null;
        }
    }

    public static Vec3d getLookPos(int maxDistance) {
        ClientPlayerEntity playerEntity = Player.getPlayer();
        float delta = MinecraftClient.getInstance().getTickDelta();

        HitResult result = playerEntity.raycast(maxDistance, delta, true);

        return result.getPos();
    }
    public static Entity getEntityLook() {
        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;

        assert hit != null;
        if (hit.getType().equals(HitResult.Type.ENTITY)) {
            EntityHitResult entityHit = (EntityHitResult) hit;
            return entityHit.getEntity();
        } else {
            return null;
        }
    }
}