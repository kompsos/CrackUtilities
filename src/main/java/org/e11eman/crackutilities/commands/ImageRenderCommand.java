package org.e11eman.crackutilities.commands;

import net.minecraft.util.math.Vec3d;
import org.e11eman.crackutilities.utilities.CClient;
import org.e11eman.crackutilities.utilities.ImageUtilities;
import org.e11eman.crackutilities.utilities.MessagePresets;
import org.e11eman.crackutilities.utilities.rendering.StaticTextScreen;
import org.e11eman.crackutilities.utilities.toolclasses.Command;
import org.e11eman.crackutilities.wrappers.Player;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ImageRenderCommand extends Command {
    public StaticTextScreen textScreen;

    public ImageRenderCommand() {
        super("image", "testing image engine", "image <width> <height> <URL>");
    }

    @Override
    public void execute(ArrayList<String> arguments) {
        Player.alertClient(MessagePresets.trueTextPreset("Generating Image!"));


        CClient.scheduler.submit(() -> {
            Vec3d position = Player.getPosition();

            textScreen = new StaticTextScreen(Integer.parseInt(arguments.get(0)), Integer.parseInt(arguments.get(1)), position.getX(), position.getY(), position.getZ(), "▪");
            BufferedImage image;

            try {
                image = ImageUtilities.urlToImage(new URL(arguments.get(2)));
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            BufferedImage resized = ImageUtilities.resize(image, textScreen.width, textScreen.height);

            textScreen.separation = 0.007625f;

            int[] pixels = resized.getRGB(0, 0, resized.getWidth(), resized.getHeight(), null, 0, resized.getWidth());
            for (int y = 0; y < resized.getHeight(); y++) {
                for (int x = 0; x < resized.getWidth(); x++) {
                    int pixel = pixels[y * resized.getWidth() + x];
                    int red = (pixel >> 16) & 255;
                    int green = (pixel >> 8) & 255;
                    int blue = pixel & 255;

                    textScreen.screen[x][y] = String.format("#%02x%02x%02x", red, green, blue);
                }
            }

            textScreen.update();
        });

    }

}
