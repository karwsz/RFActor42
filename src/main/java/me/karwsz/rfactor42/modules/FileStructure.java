package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.debug.ExceptionWindow;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


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

    private File parentDir;
    private final LinkedHashMap<File, ArrayList<File>> fileTree = new LinkedHashMap<>();


    public int getFileDepth(File file) {
        return getFileDepth(file, 0);
    }
    private int getFileDepth(File file, int depth) {
        for (Map.Entry<File, ArrayList<File>> entry : fileTree.entrySet()) {

        }
    }

    public void open(File directory) {
        this.parentDir = directory;
        addToFileTree(parentDir);
    }

    public static int MAX_FILES = 300;

    private void addToFileTree(File file) {
        if (fileTree.size() > MAX_FILES) {
            new ExceptionWindow(new IllegalStateException("Your project is too large! (more than " + MAX_FILES + ")"));
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            ArrayList<File> fileRefs = new ArrayList<>();
            assert files != null;
            for (File child : files) {
                fileRefs.add(child);
                if (child.isDirectory()) {
                    addToFileTree(file);
                }
            }
            fileTree.put(file, fileRefs);
        }
    }

    public File getParentDir() {
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

    }

}
