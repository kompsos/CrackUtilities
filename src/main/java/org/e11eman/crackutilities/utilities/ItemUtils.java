package org.e11eman.crackutilities.utilities;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.e11eman.crackutilities.wrappers.Player;

public class ItemUtils {
    public static ItemStack getMainItemStack() {
        return Player.getPlayer().getInventory().getMainHandStack();
    }

    public static NbtCompound getNBT() {
        return getMainItemStack().getNbt();
    }

    public static Item getMainItem() {
        return getMainItemStack().getItem();
    }
}
