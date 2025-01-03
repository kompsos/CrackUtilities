package org.e11eman.crackutilities.utilities.toolclasses;

import net.minecraft.item.Item;

public abstract class ItemScript {
    public String name;
    public Item displayItem;

    public ItemScript(String name, Item displayItem) {
        this.name = name;
        this.displayItem = displayItem;
    }

    public void onRightClick() {
    }

    public void onLeftClick() {
    }

    public void onItemRelease() {
    }
}
