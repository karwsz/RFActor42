package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.objects.FileComponent;
import me.karwsz.rfactor42.objects.FileTreeElement;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;


/**
 * Module responsible for managing project files and displaying them through gui
 */
public class FileStructure {

    public FileStructureGUI gui;

    public FileStructure() {
        init();
    }

    private void init() {
        this.gui = new FileStructureGUI(this);
    }

    private FileTreeElement parentDir;



    public void open(File directory) {
        this.parentDir = FileTreeElement.parent(directory);
        processFTE(parentDir, 0);
    }

    public static int MAX_FILES = 300;

    int filesAmount = 0;

    private void processFTE(FileTreeElement element, int depth) {
        filesAmount++;
        if (filesAmount > MAX_FILES) {
            new ExceptionWindow(new IllegalStateException("Your project is too large! (more than " + MAX_FILES + ")"));
            return;
        }
        if (element.file().isDirectory()) {
            File[] files = element.file().listFiles();
            ArrayList<FileTreeElement> fileRefs = new ArrayList<>();
            assert files != null;
            for (File child : files) {
                fileRefs.add(new FileTreeElement(child, depth, new ArrayList<>()));
                if (child.isDirectory()) {
                    processFTE(element, depth + 1);
                }
            }
            element.children().addAll(fileRefs);
        }
    }

    public FileTreeElement getParentDir() {
        return parentDir;
    }

    public static class FileStructureGUI extends JPanel {

        private final FileStructure fileStructure;
        public FileStructureGUI(FileStructure fileStructure) {
            this.fileStructure = fileStructure;
        }

        public void init() {
            setMinimumSize(new Dimension(150, 0));
            setPreferredSize(new Dimension(400, 0));
            setOpaque(true);
            setBorder(new LineBorder(UIManager.getColor("Component.borderColor")));
            setBackground(UIManager.getColor("Panel.background"));
        }


        int filesCount = 0;
        int depth = 0;

        private ArrayList<FileComponent> fileComponents = new ArrayList<>();

        public void setComponents() {

        }

    }

}
