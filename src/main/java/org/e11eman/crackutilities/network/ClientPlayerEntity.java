package org.e11eman.crackutilities.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.e11eman.crackutilities.wrappers.Player;

public class ClientPlayerEntity extends PlayerEntity {
    Client bot;

    public ClientPlayerEntity(Client bot, GameProfile profile) {
        super(Player.getPlayer().getWorld(), BlockPos.ORIGIN, 0.0f, profile);
        this.bot = bot;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public boolean isMainPlayer() {
        return true;
    }
}
