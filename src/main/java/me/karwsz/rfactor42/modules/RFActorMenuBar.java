package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;

import javax.swing.*;

public class RFActorMenuBar extends JMenuBar {

    public RFActorMenuBar() {
        init();
    }

    protected void init() {
        JMenu fileMenu = new JMenu(Application.localized("file"));

        //====== New ======
        JMenuItem newItem = new JMenuItem(Application.localized("new"));
        newItem.addActionListener(actionEvent -> {
            ModuleManager modules = Application.instance.moduleManager;
            if (modules.projectInfo != null) {
                if (JOptionPane.showConfirmDialog(Application.instance, Application.localized("open_project_warning")) != JOptionPane.OK_OPTION) {
                    return;
                }
            }
            String name = JOptionPane.showInputDialog(Application.instance, Application.localized("open_project_ask_name"));
            if ("".equalsIgnoreCase(name) || name == null) {
                return;
            }
            modules.openProject(name, null);
        });
        fileMenu.add(newItem); // 'New' end ; add to fileMenu

        //===== Open =====
        JMenuItem openItem = new JMenuItem(Application.localized("open"));
        openItem.addActionListener((actionEvent) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(Application.instance);
            if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
                ModuleManager modules = Application.instance.moduleManager;
                modules.openProject(null, fileChooser.getSelectedFile());
            }
        });
        fileMenu.add(openItem); // 'Open' end ; add to fileMenu


        add(fileMenu); //
    }

}
