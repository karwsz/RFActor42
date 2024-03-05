package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.modules.files.FileStructure;
import me.karwsz.rfactor42.objects.ProjectInfo;

import javax.swing.*;
import java.awt.*;

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

    public void updateFileStructure() {
        if (!fileStructureInit) {
            showFileStructure();
            fileStructureInit = true;
        }

    }

    public void showFileStructure() {
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.weightx = 1;
        gbc.weighty = 1;
        fileStructure.gui.init();
        container.add(new JScrollPane(fileStructure.gui), gbc);
        container.revalidate();
    }



}
