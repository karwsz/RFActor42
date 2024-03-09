package me.karwsz.rfactor42.modules;

import javax.swing.*;
import java.awt.*;

public class CONEditor extends JTextPane {

    public CONEditor() {
        init();
    }

    protected void init() {
        setForeground(UIManager.getColor("TextArea.foreground"));
        setBackground(UIManager.getColor("TextArea.background"));
        setMargin(new Insets(5, 10, 5, 5));
        setFont(getFont().deriveFont(15f));
    }

}
