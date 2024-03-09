package me.karwsz.rfactor42.objects;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class FileComponent extends JLabel {

    private final FileTreeElement file;

    public FileComponent(FileTreeElement file) {
        super();
        this.file = file;
        init();
    }

    private void init() {
        setOpaque(true);
        setBackground(UIManager.getColor("Panel.background"));
        setVisible(true);
        addMouseListener(new MouseAdapter() {
            final Color background = getBackground();
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(background.darker());
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(background);
                repaint();
            }
        });
    }


    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(0, 25);
    }

    public FileTreeElement getFile() {
        return file;
    }


    static Font jetBrainsMono;
    static Image folderIcon;
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        enableAntialias(g2d);
        if (file.file().isDirectory()) g2d.drawImage(folderIcon, 5 + getDepthSpace() - folderIcon.getWidth(null) / 2 - 4, getHeight() / 2 - folderIcon.getHeight(null) / 2 + 1, null);

        g2d.setFont(jetBrainsMono);
        g2d.setColor(UIManager.getColor("Label.foreground"));
        g2d.drawString(file.file().getName(), 10 + getDepthSpace(), getHeight() / 2 + getFont().getSize() / 2);
    }

    private int getDepthSpace() {
        return file.depth * 20 + 15;
    }

    private void enableAntialias(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
    }
}
