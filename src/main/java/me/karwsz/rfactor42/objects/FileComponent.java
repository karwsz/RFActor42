package me.karwsz.rfactor42.objects;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileComponent extends JPanel {

    private final File file;

    public FileComponent(File file) {
        this.file = file;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
}
