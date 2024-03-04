package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;

import javax.swing.*;
import java.awt.*;

public class RFActorMenuBar extends JMenuBar {

    public RFActorMenuBar() {
        init();
    }

    protected void init() {
        JMenu itemMenu = new JMenu(Application.loc.getString("menuBar_file"));
        add(itemMenu);
    }


}
