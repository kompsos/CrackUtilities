package org.e11eman.crackutilities.itemscripts;

import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.Exploits;
import org.e11eman.crackutilities.utilities.toolclasses.ItemScript;
import org.e11eman.crackutilities.wrappers.Player;

public class SphereGen extends ItemScript {
    public SphereGen() {
        super("Sphere Gen", Items.ECHO_SHARD);
    }

    @Override
    public void onRightClick() {
        Vec3d center = Player.getPosition();
        double radius = CClient.configSystem.getValue("itemScripting", "spheregen", "radius").getAsDouble();
        double gap = CClient.configSystem.getValue("itemScripting", "spheregen", "gap").getAsDouble();

        if(!Player.isSneaking()) {
            CClient.scheduler.submit(() -> {
                for (double theta = 0; theta < Math.PI; theta += gap) {
                    for (double phi = 0; phi < 2 * Math.PI; phi += gap) {
                        double x = center.x + radius * Math.sin(theta) * Math.cos(phi);
                        double y = center.y + radius * Math.sin(theta) * Math.sin(phi);
                        double z = center.z + radius * Math.cos(theta);

                        Exploits.entityBypassB(x, y, z, "minecraft:block_display", """
                                {shadow_radius:0f,shadow_strength:0f,transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.1f,0.1f,0.1f]},block_state:{Name:"minecraft:gray_concrete"}}
                                """);
                    }
                }
            });
        } else {
            System.out.println("Test");
        }

    }
}
