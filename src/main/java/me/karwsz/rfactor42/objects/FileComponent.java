package me.karwsz.rfactor42.objects;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class FileComponent extends JLabel {

    private final FileTreeElement file;

    public FileComponent(FileTreeElement file) {
        super(file.file().getName());
        this.file = file;
        setVisible(true);
    }

    public FileTreeElement getFile() {
        return file;
    }
}
