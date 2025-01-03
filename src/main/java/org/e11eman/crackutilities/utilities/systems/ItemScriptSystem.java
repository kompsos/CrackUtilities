package org.e11eman.crackutilities.utilities.systems;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.e11eman.crackutilities.itemscripts.SphereGen;
import org.e11eman.crackutilities.itemscripts.Minigun;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.ItemUtils;
import org.e11eman.crackutilities.utilities.toolclasses.ItemScript;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

public class ItemScriptSystem {
    public final ItemScript[] list = {
            new Minigun(),
            new SphereGen()
    };

    public void onLeftClick(CallbackInfo ci) {
        if (!(matchesItemScript() == null)) {
            matchesItemScript().onLeftClick();
            ci.cancel();
        }
    }

    public void onRightClick(CallbackInfoReturnable<net.minecraft.util.TypedActionResult<ItemStack>> cir) {
        if (!(matchesItemScript() == null)) {
            matchesItemScript().onRightClick();
            cir.setReturnValue(new TypedActionResult<>(ActionResult.FAIL, ItemUtils.getMainItemStack()));
        }
    }

    public void onItemRelease(CallbackInfo ci) {
        if (!((matchesItemScript()) == null)) {
            matchesItemScript().onItemRelease();
            ci.cancel();
        }
    }


    public ItemScriptSystem() {
        ItemGroup itemScripts = FabricItemGroup.builder()
                .icon(() -> new ItemStack(Items.DIAMOND))
                .displayName(Text.of("Item Scripts"))
                .build();

        Identifier id = new Identifier("crackutilities", "item_scripts");
        RegistryKey<ItemGroup> reg = RegistryKey.of(RegistryKeys.ITEM_GROUP, id);

        Registry.register(Registries.ITEM_GROUP, id, itemScripts);

        ItemGroupEvents.modifyEntriesEvent(reg).register(content -> {
            for (ItemScript i : list) {
                ItemStack item = i.displayItem.getDefaultStack();

                NbtCompound mod = new NbtCompound();
                mod.putString(getTag(), i.name);
                item.setNbt(mod);

                content.add(item.setCustomName(Text.literal(i.name).formatted(Formatting.BLUE)));
            }
        });
    }

    public ItemScript matchesItemScript() {
        if (ItemUtils.getMainItemStack().hasNbt()) {
            for (ItemScript i : list) {
                if (ItemUtils.getNBT().contains(getTag())) {
                    if (Objects.requireNonNull(ItemUtils.getNBT().get(getTag())).asString().matches(i.name)) {
                        return i;
                    }
                }
            }
        }
        return null;
    }

    public String getTag() {
        return CClient.configSystem.getValue("itemScripting", "tag").getAsString();
    }
}
