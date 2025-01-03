package org.e11eman.crackutilities.utilities.systems;

import com.google.gson.JsonArray;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.StringEscapeUtils;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.MessagePresets;
import org.e11eman.crackutilities.wrappers.Player;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings({"unchecked", "deprecation"})
public class ChatSystem {
    public ChatSystem() {
        CClient.events.register("clientChatSendEvent", "chatSystem", (Data) -> {
            String msg = Data[0].toString();

            if (msg.startsWith("/") || msg.isEmpty()) return;

            CallbackInfoReturnable<Boolean> cancelable = (CallbackInfoReturnable<Boolean>) Data[1];

            String prefix = CClient.configSystem.getValue("commandSystem", "prefix").getAsString();

            if (CClient.commandSystem.executeCommandIfFound(msg)) {
                MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(msg);
                cancelable.setReturnValue(true);
                return;
            } else {
                if (msg.startsWith(prefix)) {
                    Player.alertClient(MessagePresets.errorTextPreset("Command not found!"));
                    MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(msg);
                    cancelable.setReturnValue(true);
                    return;
                }
            }

            if(CClient.configSystem.getValue("chatFormatting", "enabled").getAsBoolean()) {
                JsonArray format = CClient.configSystem.getValue("chatFormatting", "formatting").getAsJsonArray();
                String username = Player.getPlayer().getGameProfile().getName();

                CClient.commandCoreSystem.run(String.format("minecraft:tellraw @a %s", format.toString()
                        .replace("%username%", username)
                        .replace("%message%", StringEscapeUtils.escapeJava(msg))));

                MinecraftClient.getInstance().inGameHud.getChatHud().addToMessageHistory(msg);
                cancelable.setReturnValue(true);
            }
        });
    }
}
