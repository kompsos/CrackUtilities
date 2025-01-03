package org.e11eman.crackutilities.utilities;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.e11eman.crackutilities.utilities.systems.*;
import org.e11eman.crackutilities.wrappers.Player;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressWarnings("unused")
public class CClient {
    public static EventSystem events = new EventSystem();
    public static ExecutorService scheduler = Executors.newCachedThreadPool();
    public static ConfigSystem configSystem;
    public static CommandSystem commandSystem;
    public static CommandCoreSystem commandCoreSystem;
    public static ChatQueueSystem chatQueueSystem;
    public static CloopSystem cloopSystem;
    public static IrcSystem ircSystem;
    public static KeybindingSystem keybindingSystem;
    public static ItemScriptSystem itemScriptSystem;
    public static BotManagerSystem botManagerSystem;

    public static void initSystems() {
        configSystem = new ConfigSystem(new File(FabricLoader.getInstance().getConfigDir() + "/CrackUtilities/config.json"));

        new ChatSystem();
        new SelfCareSystem();

        commandSystem = new CommandSystem();
        commandCoreSystem = new CommandCoreSystem();
        chatQueueSystem = new ChatQueueSystem();
        cloopSystem = new CloopSystem();
        //ircSystem = new IrcSystem();
        keybindingSystem = new KeybindingSystem();
        itemScriptSystem = new ItemScriptSystem();
        botManagerSystem = new BotManagerSystem();
    }

    public static void registerExtraEvents() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
                events.invoke("openWorld", handler, sender, client);

                Player.inWorld = true;
    });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            events.invoke("closeWorld", handler, client);

            CClient.botManagerSystem.endSync();
            Player.inWorld = false;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> events.invoke("tick"));
    }
}
