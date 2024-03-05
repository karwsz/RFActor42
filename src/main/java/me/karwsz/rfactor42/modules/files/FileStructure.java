package me.karwsz.rfactor42.modules.files;

import javax.swing.*;
import java.io.File;

public class FileStructure {

    public FileStructureGUI gui;

    public FileStructure() {
        init();
    }

    private void init() {
        this.gui = new FileStructureGUI();
    }

    public void open(File directory) {

    }

}
