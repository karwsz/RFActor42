package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class RFActorMenuBar extends JMenuBar {

    public JCheckBoxMenuItem showConFilesOnly;
    public JCheckBoxMenuItem compressCheckbox;

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

        JMenu editMenu = new JMenu(Application.localized("edit")); // start of 'Edit'
        add(editMenu); // end of 'Edit' ; add editMenu

        JMenu viewMenu = new JMenu(Application.localized("view")); // start of 'View'


        //===== 'Show .con files only' =====
        showConFilesOnly = new JCheckBoxMenuItem(Application.localized("show_.con_files_only"));
        showConFilesOnly.setState(true);
        showConFilesOnly.addActionListener((actionEvent) -> {
            Application.instance.moduleManager.fileStructure.toggleShowConFilesOnly();
        });
        viewMenu.add(showConFilesOnly); // end of 'Show .con files only' ; add to viewMenu

        add(viewMenu); // end of 'View' ; add viewMenu

        //===== 'RFA' =====
        JMenu rfaMenu = new JMenu("RFA");

        //===== 'Pack' =====
        JMenuItem packItem = new JMenuItem(Application.localized("pack"));
        packItem.addActionListener((event) -> {
            if (Application.instance.moduleManager.projectInfo == null) {
                return;
            }
            RFAModule.pack(Application.instance.moduleManager.projectSettings.shouldCompress());
        });

        rfaMenu.add(packItem);
        compressCheckbox = new JCheckBoxMenuItem(Application.localized("compress"));
        compressCheckbox.addActionListener((event) -> {
            Application.instance.moduleManager.projectSettings.setCompress(compressCheckbox.getState());
        });
        rfaMenu.add(compressCheckbox);

        //===== 'Unpack'
        JMenuItem unpackItem = new JMenuItem(Application.localized("unpack"));
        unpackItem.addActionListener((event) -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    String[] parts = f.getName().split("[.]");
                    return f.isDirectory() || parts[parts.length - 1].equalsIgnoreCase("rfa");
                }

                @Override
                public String getDescription() {
                    return Application.localized("rfaFiles");
                }
            });

            int result = fileChooser.showOpenDialog(Application.instance);
            File file = fileChooser.getSelectedFile();
            if (result != JFileChooser.APPROVE_OPTION || file == null) {
                return;
            }
            //Clone file to reuse file chooser
            file = new File(file.getAbsolutePath());

            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.resetChoosableFileFilters();

            fileChooser.setDialogTitle(Application.localized("selectOutputDir"));
            result = fileChooser.showOpenDialog(Application.instance);
            File outputDir = fileChooser.getSelectedFile();
            if (result != JFileChooser.APPROVE_OPTION || outputDir == null) {
                return;
            }

            RFAModule.unpack(file, outputDir);
        });
        rfaMenu.add(unpackItem);

        //===== SPECIAL THANKS =====
        String specialThanks = "Possible thanks to henk's RFA.py - thanks henk!";
        JMenuItem thanksItem = new JMenuItem(specialThanks);
        JMenuItem dashItem = new JMenuItem("=".repeat(specialThanks.length()));
        rfaMenu.add(new JMenuItem());
        rfaMenu.add(dashItem);
        rfaMenu.add(thanksItem);

        add(rfaMenu); // end of 'RFA'

    }

}
