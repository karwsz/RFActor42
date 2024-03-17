package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.objects.ProjectSettings;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.io.File;

public class RFActorMenuBar extends JMenuBar {

    public JCheckBoxMenuItem showConFilesOnly;
    public JCheckBoxMenuItem compressCheckbox;
    JCheckBoxMenuItem removeNonServerCheckbox;

    public RFActorMenuBar() {
        init();
    }

    protected void init() {
        JMenu fileMenu = new JMenu(Application.localized("file"));


        //For now 'New' option seems rather unpractical as it has almost same functionality as 'Open' and probably won't be used
        /*
        //====== New ======
        JMenuItem newItem = new JMenuItem(Application.localized("new"));
        newItem.addActionListener(actionEvent -> {
            ModuleManager modules = Application.instance.moduleManager;
            if (modules.projectSettings != null) {
                if (JOptionPane.showConfirmDialog(Application.instance, Application.localized("open_project_warning")) != JOptionPane.OK_OPTION) {
                    return;
                }
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(Application.localized("dirForProject"));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(Application.instance);
            if (result != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFile() == null) {
                return;
            }
            modules.openProject(fileChooser.getSelectedFile());
        });
        fileMenu.add(newItem); // 'New' end ; add to fileMenu
         */

        //===== Open =====
        JMenuItem openItem = new JMenuItem(Application.localized("open"));
        openItem.addActionListener((actionEvent) -> {
            ModuleManager modules = Application.instance.moduleManager;
            if (modules.projectSettings != null) {
                if (JOptionPane.showConfirmDialog(Application.instance, Application.localized("open_project_warning")) != JOptionPane.OK_OPTION) {
                    return;
                }
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(Application.localized("dirForProject"));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(Application.instance);
            if (result == JFileChooser.APPROVE_OPTION && fileChooser.getSelectedFile() != null) {
                modules.openProject(fileChooser.getSelectedFile());
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
            ProjectSettings.instance().toggleShowConFilesOnly();
            Application.instance.moduleManager.fileStructure.gui.setComponents();
        });
        viewMenu.add(showConFilesOnly); // end of 'Show .con files only' ; add to viewMenu

        add(viewMenu); // end of 'View' ; add viewMenu

        //===== 'RFA' =====
        JMenu rfaMenu = new JMenu("RFA");

        //===== 'Pack' =====
        JMenuItem packItem = new JMenuItem(Application.localized("pack"));
        packItem.addActionListener((event) -> {
            if (ProjectSettings.instance() == null) {
                JOptionPane.showMessageDialog(Application.instance, Application.localized("openProjectBeforeProceeding"));
                return;
            }
            if (RFAModule.isPacking()) {
                JOptionPane.showMessageDialog(Application.instance, Application.localized("busyPacking"));
                return;
            }
            if (ProjectSettings.instance().getRFABaseDirectory() == null) {
                JOptionPane.showMessageDialog(Application.instance, Application.localized("baseDirInstructions"));
                return;
            }
            RFAModule.pack(ProjectSettings.instance().shouldCompress(), ProjectSettings.instance().shouldRemoveNonServer(), () -> {
                JOptionPane.showMessageDialog(Application.instance, Application.localized("packDone"), "", JOptionPane.PLAIN_MESSAGE);
            });
        });

        rfaMenu.add(packItem);
        
        //===== 'Compress' =====
        compressCheckbox = new JCheckBoxMenuItem(Application.localized("compress"));
        compressCheckbox.setState(true);
        compressCheckbox.addActionListener((event) -> {
            ProjectSettings settings = Application.instance.moduleManager.projectSettings;
            if (settings == null) {
                JOptionPane.showMessageDialog(Application.instance, Application.localized("openProjectBeforeProceeding"));
                compressCheckbox.setState(true);
                return;
            }
            settings.setCompress(compressCheckbox.getState());
        });
        
        //===== 'Remove non-server files?' =====

        removeNonServerCheckbox = new JCheckBoxMenuItem(Application.localized("removeNonServer"));
        removeNonServerCheckbox.setState(true);
        removeNonServerCheckbox.addActionListener((event) -> {
            ProjectSettings settings = ProjectSettings.instance();
            if (settings == null) {
                JOptionPane.showMessageDialog(Application.instance, Application.localized("openProjectBeforeProceeding"));
                removeNonServerCheckbox.setState(true);
                return;
            }
            settings.setRemoveNonServer(removeNonServerCheckbox.getState());
        });

        // ----- ! Compress and Remove non-server checkboxes are  added after 'Unpack' option ! -----

        //===== 'Unpack' =====
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
        rfaMenu.add(unpackItem); // add 'Unpack' option to RFA menu
        rfaMenu.add(compressCheckbox); // add 'Compress' checkbox
        rfaMenu.add(removeNonServerCheckbox); // add 'Remove non-server' checkbox

        //===== SPECIAL THANKS =====
        String specialThanks = "Possible thanks to henk's RFA.py - thanks henk!";
        JMenuItem thanksItem = new JMenuItem(specialThanks);
        JMenuItem dashItem = new JMenuItem("=".repeat(specialThanks.length()));
        rfaMenu.add(new JMenuItem());
        rfaMenu.add(dashItem);
        rfaMenu.add(thanksItem);

        add(rfaMenu); // end of 'RFA'


        //===== 'Transfer' =====
        JMenu transferMenu = new JMenu(Application.localized("transfer"));

        //===== 'Credentials' =====
        JMenuItem addCredentialsItem = new JMenuItem(Application.localized("addcredentials"));
        addCredentialsItem.addActionListener((e) -> {
            TransferModule.CredentialsManagerGUI.get().setVisible(true);
        });
        transferMenu.add(addCredentialsItem); // 'Transfer' <- 'Credentials'

        //===== 'Send' =====
        JMenuItem sendItem = new JMenuItem(Application.localized("send"));
        sendItem.addActionListener((e) -> {
            if (Application.instance.moduleManager.projectSettings == null) {
                JOptionPane.showMessageDialog(null, Application.localized("openProjectBeforeProceeding"));
                return;
            }
            TransferModule.showSendGUI();
        });
        transferMenu.add(sendItem); // 'Transfer' <- 'Send'


        add(transferMenu); // end of 'Transfer'
    }

}
