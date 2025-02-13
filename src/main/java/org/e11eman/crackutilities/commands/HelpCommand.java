package org.e11eman.crackutilities.commands;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.MessagePresets;
import org.e11eman.crackutilities.utilities.toolclasses.Command;
import org.e11eman.crackutilities.wrappers.Player;

import java.util.ArrayList;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("help", "Get a list of all client commands", "help");
    }

    @Override
    public void execute(ArrayList<String> arguments) {
        MutableText message = MessagePresets.normalPrefix.copy().append(Text.literal(": ").formatted(Formatting.GRAY));
        Command[] list = CClient.commandSystem.commands;

        for (int i = 0; i < list.length; i++) {
            String content;

            if (i + 1 == list.length) {
                content = list[i].name + " ";
            } else {
                content = list[i].name + ", ";
            }

            int finalI = i;
            message.append(
                    Text.literal(content)
                            .formatted(Formatting.BLUE)
                            .styled(style -> style.withHoverEvent(
                                    new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                            Text.literal("Description")
                                                    .formatted(Formatting.GRAY)
                                                    .append(Text.literal(": ")
                                                            .formatted(Formatting.GRAY)
                                                    )
                                                    .append(Text.literal(list[finalI].description + "\n")
                                                            .formatted(Formatting.BLUE)
                                                    )
                                                    .append(
                                                            Text.literal("Usage")
                                                                    .formatted(Formatting.GRAY)
                                                                    .append(Text.literal(": ")
                                                                            .formatted(Formatting.GRAY)
                                                                    )
                                                                    .append(Text.literal(list[finalI].example)
                                                                            .formatted(Formatting.BLUE)
                                                                    )
                                                    )
                                    ))
                            ));
        }

        Player.alertClient(message);
    }
}
