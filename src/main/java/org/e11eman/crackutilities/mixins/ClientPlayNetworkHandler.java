package org.e11eman.crackutilities.mixins;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.wrappers.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(net.minecraft.client.network.ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandler {
    @Shadow public abstract ClientConnection getConnection();

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    public void onOnGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        CClient.events.invoke("serverGameMessage", packet, ci);
    }

    @Inject(method = "onPlaySound", at = @At("HEAD"))
    public void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        CClient.events.invoke("playSound", packet);
    }

    @Inject(method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"))
    public void sendPacket(Packet<?> packet, CallbackInfo ci) {
        if(getConnection() == Player.getNetworkHandler().getConnection()) {
            CClient.events.invoke("c2s", packet);
        }
    }
}

