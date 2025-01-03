package org.e11eman.crackutilities.keybinds;

import net.minecraft.util.math.BlockPos;
import org.e11eman.crackutilities.utilities.toolclasses.Keybind;
import org.e11eman.crackutilities.utilities.toolclasses.RaycastUtilities;
import org.e11eman.crackutilities.wrappers.Player;

public class TeleportLook extends Keybind {
    public TeleportLook() {
        super("tpLook");
    }

    @Override
    public void execute() {
        BlockPos target = RaycastUtilities.getBlockPosLook();

        if(target == null) return;
        Player.getPlayer().setPosition(target.getX(), target.getY() + 1, target.getZ());
    }
}
