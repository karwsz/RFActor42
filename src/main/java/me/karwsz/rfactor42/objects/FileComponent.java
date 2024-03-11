package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;

import javax.imageio.ImageIO;
import javax.management.Attribute;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.util.Map;
import java.util.jar.Attributes;

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

            @Override
            public void mouseClicked(MouseEvent e) {
                if (file.isCONFile() && e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                    Application.instance.moduleManager.getCONEditor().setText(readFully());
                }
            }
        });
    }

    public String readFully() {
        try {
           return Files.readString(file.file().toPath());
        } catch (
                IOException e) {
            RuntimeException runtimeException = new RuntimeException(e);
            new ExceptionWindow(runtimeException);
            throw runtimeException;
        }
    }



    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(prefWidth, 25);
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

    private int prefWidth = 0;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        Font jetbrainsFont = jetBrainsMono;

        enableAntialias(g2d);
        if (file.isDirectory()) {
            if (this.equals(ProjectSettings.instance().getRFABaseDirectory())) {
                Map<TextAttribute, Object> attributes = (Map<TextAttribute, Object>) jetbrainsFont.getAttributes();
                attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                jetbrainsFont = jetbrainsFont.deriveFont(attributes);

                g2d.setColor(new Color(239, 147, 38));
                g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
                int stringWidth = g2d.getFontMetrics().stringWidth("RFA");
                g2d.drawString("RFA", 8 + getDepthSpace() - stringWidth, getHeight() / 2 + getFont().getSize() / 2);

            }
            else g2d.drawImage(folderIcon, 5 + getDepthSpace() - folderIcon.getWidth(null) / 2 - 4, getHeight() / 2 - folderIcon.getHeight(null) / 2 + 1, null);
        }
        else if (file.isCONFile()) {
            g2d.setColor(Color.decode("#009900"));
            g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 11));
            int stringWidth = g2d.getFontMetrics().stringWidth("CON");
            g2d.drawString("CON", 7 + getDepthSpace() - stringWidth, getHeight() / 2 + getFont().getSize() / 2);
        }

        g2d.setFont(jetbrainsFont);
        g2d.setColor(UIManager.getColor("Label.foreground"));

        //TODO: find a way to make this outside of paintComponent, once
        prefWidth = g2d.getFontMetrics().stringWidth(file.file().getName()) + 20 + getDepthSpace();



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
