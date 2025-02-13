package org.e11eman.crackutilities.mixins;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface ClientAccessor {
    @Mutable
    @Accessor("session")
    void setSession(Session session);
}
