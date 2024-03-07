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
        filesAmount = 0;
        this.parentDir = FileTreeElement.parent(directory);
        addAllChildrenToElement(parentDir);
    }

    public static int MAX_FILES = 500;

    int filesAmount = 0;

    private void addAllChildrenToElement(FileTreeElement element) {
        filesAmount++;
        if (filesAmount > MAX_FILES) {
            IllegalStateException exception = new IllegalStateException("Your project is too large! (It's over " + MAX_FILES + "!)");
            new ExceptionWindow(exception);
            filesAmount = 0;
            throw exception;
        }
        if (element.file().isDirectory()) {
            File[] files = element.file().listFiles();
            ArrayList<FileTreeElement> fileRefs = new ArrayList<>();
            assert files != null;
            for (File child : files) {
                FileTreeElement childElement = new FileTreeElement(element, child);
                fileRefs.add(childElement);
                if (child.isDirectory()) {
                    addAllChildrenToElement(childElement);
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
            setLayout(new GridBagLayout());
        }


        private final ArrayList<FileComponent> fileComponents = new ArrayList<>();

        private GridBagConstraints gbc;

        public void setComponents() {
            gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.anchor = GridBagConstraints.FIRST_LINE_START;
            gbc.fill = GridBagConstraints.NONE;
            for (Component component : getComponents()) {
                remove(component);
            }
            fileComponents.clear();
            for (FileTreeElement.FTEIterator it = fileStructure.parentDir.iterator(); it.hasNext(); ) {
                FileTreeElement element = it.next();
                System.out.println(element.displayString());
                FileComponent component = new FileComponent(element);
                gbc.gridx = element.depth;
                System.out.println(gbc.gridx + " | " + gbc.gridy);
                add(component, gbc);
                gbc.gridy++;
            }
            gbc.gridy++;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
            add(new JPanel(), gbc);
            revalidate();
            repaint();
        }

    }

}
