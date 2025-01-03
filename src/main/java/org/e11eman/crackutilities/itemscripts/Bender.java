package org.e11eman.crackutilities.itemscripts;

import net.minecraft.block.Block;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import org.e11eman.crackutilities.utilities.toolclasses.ItemScript;
import org.e11eman.crackutilities.utilities.toolclasses.RaycastUtilities;

public class Bender extends ItemScript {
    public Bender() {
        super("Shard of Bending", Items.AMETHYST_SHARD);
    }

    @Override
    public void onRightClick() {
        BlockPos selectedBlockPos = RaycastUtilities.getBlockPosLook();
        Block selectedBlock = RaycastUtilities.getBlockLook();

        if(selectedBlock != null) {
            //CClient.commandCoreSystem.run(String.format("minecraft:setblock %s %s %s minecraft:air", selectedBlockPos.getX(), selectedBlockPos.getY(), selectedBlockPos.getZ()));
        }
    }
}
