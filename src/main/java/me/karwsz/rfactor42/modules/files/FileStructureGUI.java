package me.karwsz.rfactor42.modules.files;

import com.formdev.flatlaf.ui.FlatTitlePane;
import me.karwsz.rfactor42.Application;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class FileStructureGUI extends JPanel {
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
