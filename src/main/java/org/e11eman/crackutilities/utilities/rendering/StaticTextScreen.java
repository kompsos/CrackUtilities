package org.e11eman.crackutilities.utilities.rendering;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.SecureRandomStuff;
import org.e11eman.crackutilities.wrappers.Player;

import java.util.ArrayList;

public class StaticTextScreen extends Screen {
    public double x;
    public double y;
    public double z;
    public float separation = 0.125f;

    public StaticTextScreen(int width, int height, double x, double y, double z, String character) {
        super(width, height, character);

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void update() {
        double startY = y;

        for(int i = 0; i < this.height; i++) {
            String randomizedTag = SecureRandomStuff.getRandomString(6) + Player.getPlayer().getGameProfile().getName();
            ArrayList<JsonArray> names = new ArrayList<>();

            for(int y = 0; y < height; y++) {
                JsonArray name = new JsonArray();

                for(int x = 0; x < width; x++) {
                    JsonObject pixel = new JsonObject();

                    pixel.add("text", new JsonPrimitive(this.character));
                    pixel.add("color", new JsonPrimitive(screen[x][y]));

                    name.add(pixel);
                }

                names.add(name);
            }

            startY -= separation;

            CClient.commandCoreSystem.run(String.format("summon text_display %s %s %s %s", x + 18, startY, z + 18,
                    "{Tags:[\"" + randomizedTag + "\"],text:'" + names.get(i).getAsJsonArray() + "',see_through: 0b,line_width: 64000, background: false, text_opacity:-1, transformation:{left_rotation:[0f,0f,0f,1f],right_rotation:[0f,0f,0f,1f],translation:[0f,0f,0f],scale:[0.125f,0.125f,0.125f]}, brightness: {sky: 15, block:15}, billboard:\"center\"}"));

            CClient.commandCoreSystem.run(
                    String.format("execute as @e[limit=1,tag=%s] run data modify entity @s Pos set value [%sd,%sd,%sd]",randomizedTag, x, startY, z)
            );
        }
    }

    @Override
    public void draw() {}
}
