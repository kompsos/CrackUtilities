package org.e11eman.crackutilities.mixins;

import org.e11eman.crackutilities.utilities.CClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("unused")
@Mixin(net.minecraft.client.gui.screen.ChatScreen.class)
public class ChatScreen {
    @Inject(at = @At("HEAD"), method = "sendMessage", cancellable = true)
    public void sendMessage(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> cir) {

        CClient.events.invoke("clientChatSendEvent", chatText, cir);
    }
}
