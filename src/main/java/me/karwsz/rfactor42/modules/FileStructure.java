package me.karwsz.rfactor42.modules;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;


/**
 * Module responsible for managing project files and displaying them through gui
 */
public class FileStructure {

    public FileStructureGUI gui;

    public FileStructure() {
        init();
    }

    private void init() {
        this.gui = new FileStructureGUI();
    }

    public void open(File directory) {

    }

    public static class FileStructureGUI extends JPanel {
        public FileStructureGUI() {

        }

        public void init() {
            setMinimumSize(new Dimension(150, 0));
            setPreferredSize(new Dimension(400, 0));
            setOpaque(true);
            setBorder(new LineBorder(UIManager.getColor("Component.borderColor")));
            setBackground(UIManager.getColor("Panel.background"));
        }

    }

}
