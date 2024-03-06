package me.karwsz.rfactor42.objects;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileComponent extends JLabel {

    private final FileTreeElement file;

    public FileComponent(FileTreeElement file) {
        this.file = file;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(UIManager.getColor("Label.foreground"));
        g2d.drawString(file.file().getName(), getWidth() / 2, getHeight() / 2);
        super.paint(g);
    }
}
