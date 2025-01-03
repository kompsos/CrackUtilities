package org.e11eman.crackutilities.commands;

import org.e11eman.crackutilities.network.Client;
import org.e11eman.crackutilities.utilities.ArrayTools;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.SecureRandomStuff;
import org.e11eman.crackutilities.utilities.toolclasses.Command;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class BotManagerCommand extends Command {
    public BotManagerCommand() {
        super("botManager", "Manage clients bot system", """
                botManager <add> <username>
                botManager <remove> <username>
                botManager <removeAll>
                botManager <chatAll> <message>
                botManager <clientSync> <line/circle/start/end/pinpoint>
                botManager <syncPos>
                botManager <massAdd> <num>
                """);
    }


    @Override
    public void execute(ArrayList<String> arguments) throws IOException {
        ArrayList<Client> clients = CClient.botManagerSystem.clients;

        switch (arguments.get(0)) {
            case "add" -> CClient.botManagerSystem.newBot(arguments.get(1));

            case "remove" -> {
                for (int i = 0; i < clients.size(); i++) {
                    if(Objects.equals(clients.get(i).username, arguments.get(1))) {
                        clients.get(i).disconnect();
                        clients.remove(clients.get(i));
                    }
                }
            }

            case "removeAll" -> {
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).disconnect();
                    clients.remove(clients.get(i));
                }
            }

            case "massAdd" -> {
                int count = Integer.parseInt(arguments.get(1));

                CClient.scheduler.submit(() -> {
                    for (int i = 0; i < count; i++) {
                        try {
                            Thread.sleep(6000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        CClient.botManagerSystem.newBot(SecureRandomStuff.getRandomString(8));
                    }
                });

            }

            case "chatAll" -> {
                arguments.remove(0);
                for (Client client : clients) {
                    client.sendChat(ArrayTools.join(arguments, ' '));
                }
            }

            case "syncPos" -> CClient.botManagerSystem.moveAllToPlayer();

            case "clientSync" -> {
                switch (arguments.get(1)) {
                    case "line" -> CClient.botManagerSystem.shape = "line";
                    case "circle" -> CClient.botManagerSystem.shape = "circle";
                    case "end" -> CClient.botManagerSystem.endSync();
                    case "start" -> CClient.botManagerSystem.startSync();
                    case "pinpoint" -> CClient.botManagerSystem.pinPointLook = !CClient.botManagerSystem.pinPointLook;
                }
            }
        }
    }
}