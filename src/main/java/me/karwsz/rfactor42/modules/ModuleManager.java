package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.objects.ProjectInfo;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.io.File;

public class ModuleManager {


    public ProjectInfo projectInfo;
    public FileStructure fileStructure;

    public ModuleManager() {
        this.fileStructure = new FileStructure();
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

    public void openProject(String name, File file) {
        if (file == null) {
            projectInfo = new ProjectInfo(name, "1.0", null);
        }
        else if (!file.exists()) {
            new ExceptionWindow(new IllegalArgumentException("File does not exist"));
        }
        else {
            projectInfo = new ProjectInfo(name, "1.0", file);
        }

        fileStructure.open(file);

        updateFileStructure();
    }

    public void updateFileStructure() {
        if (!fileStructureInit) {
            showFileStructure();
            fileStructureInit = true;
        }
        else {
            fileStructure.gui.setComponents();
        }
    }

    public void showFileStructure() {
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        gbc.weightx = 1;
        gbc.weighty = 1;
        fileStructure.gui.init();
        fileStructure.gui.setComponents();
        JScrollPane scrollPane = new JScrollPane(fileStructure.gui);
        scrollPane.setMinimumSize(new Dimension(150, 0));
        scrollPane.setPreferredSize(new Dimension(450, 0));
        scrollPane.setMaximumSize(new Dimension(1000, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(30);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(30);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, new JPanel());
        splitPane.setMaximumSize(new Dimension(800, 999));

        container.add(splitPane, gbc);
        container.revalidate();
    }



}
