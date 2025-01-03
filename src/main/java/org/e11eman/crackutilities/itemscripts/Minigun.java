package org.e11eman.crackutilities.itemscripts;

import net.minecraft.client.util.math.Vector2f;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.Exploits;
import org.e11eman.crackutilities.utilities.MathExtras;
import org.e11eman.crackutilities.utilities.MessagePresets;
import org.e11eman.crackutilities.utilities.toolclasses.ItemScript;
import org.e11eman.crackutilities.wrappers.Player;
import org.joml.Vector3f;

public class Minigun extends ItemScript {
    public Minigun() {
        super("Minigun", Items.BLAZE_POWDER);
    }

    private boolean enabled = false;
    private long count = 0;

    @Override
    public void onRightClick() {
        String entity = CClient.configSystem.getValue("itemScripting", "minigun", "entity").getAsString();
        float speed = CClient.configSystem.getValue("itemScripting", "minigun", "speed").getAsFloat();

        if(!enabled) {
            CClient.events.register("tick", "minigun", (Event) -> {
                Vec3d startingPos = Player.getPosition();
                Vector2f yawAndPitch = new Vector2f(Player.getYaw(), Player.getPitch());

                Vector3f pos = MathExtras.yawPitchSpeedToVelocity(yawAndPitch.getX(), yawAndPitch.getY(), speed);

                count++;

                Exploits.entityBypassA(startingPos.x, startingPos.y + 1.5, startingPos.z, entity, String.format("{Tags:[\"minig_%s\"],Motion:[%sd, %sd, %sd]}",
                        "minig_" + count, pos.x, pos.y, pos.z
                ));
            });
         enabled = true;

         Player.alertClient(MessagePresets.trueTextPreset("Minigun Active!"));
        } else {
            CClient.events.unregisterInstance("tick", "minigun");
            enabled = false;

            Player.alertClient(MessagePresets.falseTextPreset("Minigun Deactivated!"));
        }

    }
}
