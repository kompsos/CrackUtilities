package org.e11eman.crackutilities.utilities.systems;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.socket.client.IO;
import io.socket.client.Socket;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.wrappers.Player;

import java.net.URI;

@SuppressWarnings("unused")
public class IrcSystem {
    private final Gson GSON = new Gson();
    private final JsonObject CONFIG = CClient.configSystem.getConfig();


    private final Text PREFIX = Text.literal("[").formatted(Formatting.DARK_GRAY, Formatting.BOLD)
            .append(Text.literal("IRC").formatted(Formatting.RESET).formatted(Formatting.BLUE))
            .append(Text.literal("] ").formatted(Formatting.DARK_GRAY, Formatting.BOLD));

    private final URI URI = java.net.URI.create(CClient.configSystem.getValue("ircSystem", "host").getAsString());

    private final IO.Options OPTIONS = IO.Options.builder()
            .setPort(CClient.configSystem.getValue("ircSystem", "port").getAsInt())
            .setTimeout(CClient.configSystem.getValue("ircSystem", "timeout").getAsInt())
            .build();

    public Socket socket = IO.socket(URI, OPTIONS);

    public IrcSystem() {
        socket.on(Socket.EVENT_CONNECT, args -> {
            if(Player.inWorld) {
                Player.alertClient(
                        PREFIX.copy().append(Text.literal("Connected to IRC!").formatted(Formatting.RESET).formatted(Formatting.GREEN))
                );
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, args -> {
            if(Player.inWorld) {
                Player.alertClient(
                        PREFIX.copy().append(Text.literal("Disconnected from IRC!").formatted(Formatting.RESET).formatted(Formatting.RED))
                );
            }
        });

        socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
            if(Player.inWorld) {
                Player.alertClient(
                        PREFIX.copy().append(Text.literal("Connection error!\n" + args[0]).formatted(Formatting.RESET).formatted(Formatting.RED))
                );
            }
        });

        socket.on("message", args -> {
            JsonObject data = GSON.fromJson(GSON.toJson(args[0]), JsonObject.class).getAsJsonObject("map");
            JsonObject sender = data.getAsJsonObject("sender");
            JsonObject map = sender.getAsJsonObject("map");

            String origin = map.get("origin").getAsString();
            String username = map.get("username").getAsString();
            String payload = data.get("payload").getAsString();

            if (data.getAsJsonObject("sender").getAsJsonObject("map").get("bot") != null) return;

            Player.alertClient(
                    PREFIX.copy()
                            .append(Text.literal("[").formatted(Formatting.DARK_GRAY, Formatting.BOLD))
                            .append(Text.literal(origin).formatted(Formatting.RESET).formatted(Formatting.BLUE))
                            .append(Text.literal("] ").formatted(Formatting.DARK_GRAY, Formatting.BOLD))
                            .append(Text.literal(username).formatted(Formatting.RESET).formatted(Formatting.BLUE))
                            .append(Text.literal(": ").formatted(Formatting.DARK_GRAY))
                            .append(Text.literal(payload).formatted(Formatting.RESET).formatted(Formatting.GRAY))
                    );
        });
    }


}