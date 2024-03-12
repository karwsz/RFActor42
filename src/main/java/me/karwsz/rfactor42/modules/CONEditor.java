package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.objects.EditorSession;
import me.karwsz.rfactor42.objects.FileTreeElement;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CONEditor extends JEditorPane {


    private final ArrayList<EditorSession> sessions = new ArrayList<>();

    private EditorSession activeSession;

    public CONEditor() {
        init();
    }

    protected void init() {
        setForeground(UIManager.getColor("TextArea.foreground"));
        setBackground(UIManager.getColor("TextArea.background"));
        setMargin(new Insets(5, 10, 5, 5));
        setFont(getFont().deriveFont(15f));

        setupUndoRedo();
        setupSave();
    }

    private void setupSave() {
        KeyStroke saveKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        getInputMap().put(saveKeystroke, "saveKeystroke");
        getActionMap().put("saveKeystroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (activeSession != null) {
                    updateSession();
                    activeSession.write();
                }
            }
        });
    }

    private void setupUndoRedo() {
        KeyStroke undoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        KeyStroke redoKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK);

        getInputMap().put(undoKeystroke, "undoKeystroke");
        getActionMap().put("undoKeystroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoManager undoManager = activeSession.getManager();
                try {
                    if (undoManager.canUndo()) undoManager.undo();
                } catch (CannotUndoException ignore) {}
            }
        });

        getInputMap().put(redoKeystroke, "redoKeystroke");
        getActionMap().put("redoKeystroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoManager undoManager = activeSession.getManager();
                try {
                    if (undoManager.canRedo()) undoManager.redo();
                } catch (CannotRedoException ignore) {}
            }
        });
    }

    public EditorSession getSession(FileTreeElement element) {
        return sessions.stream().filter(editorSession -> editorSession.getElement().equals(element)).findAny().orElse(null);
    }

    public EditorSession getOrCreateSession(FileTreeElement fileTreeElement) {
        EditorSession session = getSession(fileTreeElement);
        if (session == null) {
            session = new EditorSession(fileTreeElement);
            sessions.add(session);
        }
        return session;
    }

    public void loadFile(FileTreeElement file) {
        if (activeSession != null) {
            getDocument().removeUndoableEditListener(activeSession.getManager());
            updateSession();
        }

        activeSession = getOrCreateSession(file);
        setText(activeSession.getText());
        getDocument().addUndoableEditListener(activeSession.getManager());
    }

    private void updateSession() {
        activeSession.setText(getText());
    }
}
