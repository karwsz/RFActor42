package me.karwsz.rfactor42.util;

import me.karwsz.rfactor42.objects.FileComponent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Assets {
    public static Font jetBrainsMono;
    public static Image folderIcon;
    static {
        try {
            jetBrainsMono = Font.createFont(Font.PLAIN, FileComponent.class.getResourceAsStream("/fonts/JetBrainsMono-Regular.ttf"));
            folderIcon = ImageIO.read(FileComponent.class.getResourceAsStream("/icons/icons8-folder-64-yellow.png"));
        } catch (
                FontFormatException |
                IOException e) {
            throw new RuntimeException(e);
        }
        jetBrainsMono = jetBrainsMono.deriveFont( 11.75f);


        folderIcon = folderIcon.getScaledInstance(16, 16, Image.SCALE_REPLICATE);

    }

}
