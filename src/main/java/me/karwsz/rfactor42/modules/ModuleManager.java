package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.modules.editor.CONEditor;
import me.karwsz.rfactor42.objects.ProjectSettings;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ModuleManager {


    public RFActorMenuBar RFActorMenuBar;
    public ProjectSettings projectSettings;
    public FileStructure fileStructure;
    private CONEditor conEditor;

    public TransferModule transferModule;

    public ModuleManager() {
        this.RFActorMenuBar = new RFActorMenuBar();
        this.fileStructure = new FileStructure();
        this.transferModule = new TransferModule();
    }

    private final GridBagConstraints gbc = new GridBagConstraints();


    private Container container;
    public void attach(Container container) {
        if (this.container != null) {
            throw new IllegalStateException("Already attached to container");
        }
        this.container = container;
        container.setLayout(new GridBagLayout());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
    }

    private boolean fileStructureInit = false;

    public void openProject(File file) {
        if (!file.exists()) {
            new ExceptionWindow(new IllegalArgumentException("File does not exist"));
        }
        else {
            projectSettings = new ProjectSettings(file);
        }

        projectSettings.parseFile();

        RFActorMenuBar.compressCheckbox.setState(projectSettings.shouldCompress());
        RFActorMenuBar.removeNonServerCheckbox.setState(projectSettings.shouldRemoveNonServer());
        RFActorMenuBar.showConFilesOnly.setState(projectSettings.shouldShowConFilesOnly());

        fileStructure.open(file);

        if (conEditor != null) conEditor.reset();

        Application.globalSettings.addValue("addrecentproject", file.getAbsolutePath());
        RFActorMenuBar.updateOpenRecentMenu();
        updateFileStructure();
    }

    public void updateFileStructure() {
        if (!fileStructureInit) {
            showEditingComponents();
            fileStructureInit = true;
        }
        else {
            fileStructure.gui.setComponents();
        }
    }

    private void showEditingComponents() {
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 0.3;
        gbc.weighty = 1;
        fileStructure.gui.init();
        fileStructure.gui.setComponents();

        JScrollPane scrollPane = new JScrollPane(fileStructure.gui);
        scrollPane.setMinimumSize(new Dimension(150, 0));
        scrollPane.setPreferredSize(new Dimension(450, 0));
        scrollPane.setMaximumSize(new Dimension(1000, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(30);


        conEditor = new CONEditor();



        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, conEditor);
        splitPane.setMaximumSize(new Dimension(800, 999));


        container.add(splitPane, gbc);
        container.revalidate();
    }

    public CONEditor getCONEditor() {
        return conEditor;
    }
}
