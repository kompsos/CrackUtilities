package org.e11eman.crackutilities.utilities.systems;

import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.wrappers.Player;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChatQueueSystem {
    private final ArrayList<String> messages = new ArrayList<>();
    private Timer queue = new Timer();

    public ChatQueueSystem() {
        CClient.events.register("openWorld", "chatQueueOpenWorld",  (Event) -> queue.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!messages.isEmpty()) {
                    String message = messages.get(messages.size() - 1);
                    Player.sendChat(message);
                    messages.remove(messages.size() - 1);
                }
            }
        }, 0, CClient.configSystem.getValue("chatQueueSystem", "delay").getAsLong()));

        CClient.events.register("closeWorld", "chatQueueCloseWorld",  (Event) -> {
           queue.purge();
           queue.cancel();

           messages.clear();

           queue = new Timer();
        });
    }

    public void addMessageToQueue(String message) {
        messages.add(message);
    }
}
