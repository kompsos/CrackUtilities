package org.e11eman.crackutilities.commands;

import net.minecraft.client.network.PlayerListEntry;
import org.e11eman.crackutilities.utilities.ArrayTools;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.toolclasses.Command;
import org.e11eman.crackutilities.wrappers.Player;

import java.util.ArrayList;

public class SudoAllCommand extends Command {
    public SudoAllCommand() {
        super("sudoall", "Sudos every player in the server", "sudoall <command>");
    }

    @Override
    public void execute(ArrayList<String> arguments) {
        for(PlayerListEntry player : Player.getPlayerList()) {
            if(player.getProfile().getId().toString().equals(Player.getUuid())) return;
            CClient.commandCoreSystem.run(String.format("sudo %s %s", player.getProfile().getId().toString(), ArrayTools.join(arguments, ' ')));
        }
    }
}
