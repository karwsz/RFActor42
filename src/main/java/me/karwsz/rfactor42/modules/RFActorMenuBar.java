package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RFActorMenuBar extends JMenuBar {

    public RFActorMenuBar() {
        init();
    }

    protected void init() {
        JMenu itemMenu = new JMenu(Application.loc.getString("file"));

        JMenuItem newItem = new JMenuItem(Application.loc.getString("new"));
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ModuleManager modules = Application.instance.moduleManager;
                modules.fileStructure.open(null);
                modules.updateFileStructure();
            }
        });

        itemMenu.add(newItem); //
        add(itemMenu); //
    }


}
