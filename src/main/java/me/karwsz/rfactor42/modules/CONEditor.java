package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.objects.FileTreeElement;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashMap;

public class CONEditor extends JEditorPane {


    private final HashMap<FileTreeElement, UndoManager> undoManagers = new HashMap<>();

    UndoManager undoManager;
    public CONEditor() {
        init();
    }

    protected void init() {
        setForeground(UIManager.getColor("TextArea.foreground"));
        setBackground(UIManager.getColor("TextArea.background"));
        setMargin(new Insets(5, 10, 5, 5));
        setFont(getFont().deriveFont(15f));

        setupUndoRedo();
    }

    private void setupUndoRedo() {
        KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);

        getInputMap().put(undoKeystroke, "undoKeystroke");
        getActionMap().put("undoKeystroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canUndo()) undoManager.undo();
                } catch (CannotUndoException ignore) {}
            }
        });

        getInputMap().put(redoKeystroke, "redoKeystroke");
        getActionMap().put("redoKeystroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (undoManager.canRedo()) undoManager.redo();
                } catch (CannotRedoException ignore) {}
            }
        });
    }

    public void loadFile(FileTreeElement file) {
        getDocument().removeUndoableEditListener(undoManager);
        undoManagers.putIfAbsent(file, new UndoManager() {

            @Override
            public synchronized boolean addEdit(UndoableEdit anEdit) {
                if (anEdit instanceof DocumentEvent event) {
                    if (event.getLength() == 1) {
                        Element element = getDocument().getDefaultRootElement();
                        AbstractDocument.LeafElement content = (AbstractDocument.LeafElement) element.getElement(0);

                        System.out.println(content.getClass());
                    }
                }
                return super.addEdit(anEdit);
            }
        });
        undoManager = undoManagers.get(file);
        undoManager.setLimit(300);
        setText(file.readFully());
        getDocument().addUndoableEditListener(undoManager);
    }
}
