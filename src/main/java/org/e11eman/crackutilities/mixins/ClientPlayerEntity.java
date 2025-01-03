package org.e11eman.crackutilities.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.MovementType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.wrappers.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(net.minecraft.client.network.ClientPlayerEntity.class)
public class ClientPlayerEntity {
    @Shadow @Final protected MinecraftClient client;

    @Inject(method = "move", at = @At("HEAD"))
    public void move(MovementType movementType, Vec3d movement, CallbackInfo ci) {
        CClient.events.invoke("playerMovement", Player.getPlayer().getPos());
    }

    @Inject(at = @At("HEAD"), method = "swingHand", cancellable = true)
    public void swingHand(Hand hand, CallbackInfo ci) {
        if(client == MinecraftClient.getInstance()) {
            CClient.itemScriptSystem.onLeftClick(ci);
        }
    }
}
