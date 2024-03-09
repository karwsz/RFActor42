package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;

import javax.swing.*;

public class RFActorMenuBar extends JMenuBar {

    private JCheckBoxMenuItem showConFilesOnly;

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
            ModuleManager modules = Application.instance.moduleManager;
            if (modules.projectInfo != null) {
                if (JOptionPane.showConfirmDialog(Application.instance, Application.localized("open_project_warning")) != JOptionPane.OK_OPTION) {
                    return;
                }
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(Application.instance);
            if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
                modules.openProject(null, fileChooser.getSelectedFile());
            }
        });
        fileMenu.add(openItem); // 'Open' end ; add to fileMenu


        add(fileMenu); // end of 'File' ; add fileMenu

        JMenu editMenu = new JMenu("Edit"); // start of 'Edit'
        add(editMenu); // end of 'Edit' ; add editMenu

        JMenu viewMenu = new JMenu("View"); // start of 'View'


        //===== 'Show .con files only' =====
        showConFilesOnly = new JCheckBoxMenuItem("Show .con files only");
        showConFilesOnly.setState(true);
        showConFilesOnly.addActionListener((actionEvent) -> {
            Application.instance.moduleManager.fileStructure.toggleShowConFilesOnly();
        });
        viewMenu.add(showConFilesOnly); // end of 'Show .con files only' ; add to viewMenu

        add(viewMenu); // end of 'View' ; add viewMenu
    }

}
