package org.e11eman.crackutilities.utilities.systems;

import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.wrappers.Player;

import java.util.Timer;
import java.util.TimerTask;

public class SelfCareSystem {
    private Timer careLoop = new Timer();

    public SelfCareSystem() {
        CClient.events.register("openWorld", "selfCareOpenWorld", (Event) -> {
            long delay = CClient.configSystem.getValue("selfCareSystem", "delay").getAsLong();
            boolean checkCreative = CClient.configSystem.getValue("selfCareSystem", "checkCreative").getAsBoolean();
            boolean checkOperator = CClient.configSystem.getValue("selfCareSystem", "checkOperator").getAsBoolean();

            careLoop.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if(checkCreative) {
                        if(!Player.getPlayer().isCreative()) {
                            Player.sendChat("/minecraft:gamemode creative " + Player.getUsername());
                        }
                    }

                    if(checkOperator) {
                        if(!Player.getPlayer().hasPermissionLevel(4)) {
                            Player.sendChat("/minecraft:op @s[type=player]");
                        }
                    }
                }
            }, 0, delay);
        });

        CClient.events.register("closeWorld", "selfCareCloseWorld",  (Event) -> {
            careLoop.purge();
            careLoop.cancel();

            careLoop = new Timer();
        });
    }
}
