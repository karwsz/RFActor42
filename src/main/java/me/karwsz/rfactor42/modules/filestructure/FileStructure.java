package me.karwsz.rfactor42.modules.filestructure;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.objects.FileComponent;
import me.karwsz.rfactor42.objects.FileTreeElement;
import me.karwsz.rfactor42.objects.ProjectSettings;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    int filesAmount;

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
        private JPopupMenu dirPopupMenu;

        public FileStructureGUI(FileStructure fileStructure) {
            this.fileStructure = fileStructure;
        }


        public void init() {
            setOpaque(true);
            setBackground(UIManager.getColor("Panel.background"));
            setBorder(new CompoundBorder(new LineBorder(UIManager.getColor("Component.borderColor")),
                    new EmptyBorder(new Insets(0, 5, 0, 0))));
            setLayout(new GridBagLayout());
            createDirectoryPopupMenu();
        }

        private FileComponent activeComponent;

        private void createDirectoryPopupMenu() {
            this.dirPopupMenu = new JPopupMenu();

            JMenuItem markAsBaseDir = new JMenuItem(Application.localized("markAsBase"));
            markAsBaseDir.addActionListener((event) -> {
                Application.instance.moduleManager.projectSettings.setRFABaseDirectory(activeComponent.getFile().file());
                repaint();
            });

            this.dirPopupMenu.add(markAsBaseDir);
        }

        @Override
        public Dimension getPreferredSize() {
            int maxWidth = 0;
            for (FileComponent fileComponent : fileComponents) {
                if (fileComponent.getPreferredSize().width > maxWidth) maxWidth = fileComponent.getPreferredSize().width;
            }
            return new Dimension(maxWidth, fileComponents.size() * fileComponents.get(0).getPreferredSize().height);
        }

        private final ArrayList<FileComponent> fileComponents = new ArrayList<>();

        public void setComponents() {
            if (fileStructure.getParentDir() == null) return;
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridy = 0;
            gbc.gridx = 0;
            gbc.weightx = 0;
            gbc.weighty = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            for (Component component : getComponents()) {
                remove(component);
            }
            fileComponents.clear();
            for (FileTreeElement.FTEIterator it = fileStructure.parentDir.iterator(); it.hasNext();) {
                FileTreeElement element = it.next();
                if (!element.isDirectory() && !element.isCONFile() && ProjectSettings.instance().shouldShowConFilesOnly()) {
                    continue;
                }
                FileComponent component = createFileComponent(element);
                fileComponents.add(component);
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

        private FileComponent createFileComponent(FileTreeElement element) {
            FileComponent component = new FileComponent(element);
            if (element.isDirectory()) {
                component.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON3) {
                            activeComponent = (FileComponent) e.getComponent();
                        }
                    }
                });
                component.setComponentPopupMenu(dirPopupMenu);
            }
            return component;
        }

    }

}
