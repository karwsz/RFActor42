package me.karwsz.rfactor42.modules.editor;

import me.karwsz.rfactor42.objects.EditorSession;
import me.karwsz.rfactor42.objects.FileTreeElement;
import me.karwsz.rfactor42.util.Assets;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class CONEditor extends JPanel {


    private final ArrayList<EditorSession> sessions = new ArrayList<>();

    private EditorSession activeSession;
    private JScrollPane editorScrollPane;
    private JEditorPane editorPane;
    private DefaultStyledDocument document;

    public CONEditor() {
        init();
    }

    protected void init() {
        setMinimumSize(new Dimension(300, 0));
        setPreferredSize(new Dimension(500, 0));
        setMaximumSize(new Dimension(1000, 0));

        this.editorPane = new JEditorPane() {
            @Override
            public Dimension getPreferredSize() {
                Dimension pref = super.getPreferredSize();
                return new Dimension(pref.width - editorScrollPane.getHorizontalScrollBar().getWidth(), pref.height);
            }
        };

        document = new DefaultStyledDocument();
        editorPane.setDocument(document);

        editorPane.setForeground(UIManager.getColor("TextArea.foreground"));
        editorPane.setBackground(UIManager.getColor("TextArea.background"));
        editorPane.setMargin(new Insets(5, 10, 5, 5));
        editorPane.setFont(Assets.jetBrainsMono.deriveFont(14f));
        editorPane.setOpaque(true);

        setupUndoRedo();
        setupSave();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        editorScrollPane = new JScrollPane(editorPane);
        editorScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        editorScrollPane.getVerticalScrollBar().setUnitIncrement(15);
        editorScrollPane.getHorizontalScrollBar().setUnitIncrement(15);

        add(editorScrollPane, gbc);

    }

    public void reset() {
        sessions.clear();
        activeSession = null;
        editorPane.setText("");
    }

    private void setupSave() {
        KeyStroke saveKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK);
        editorPane.getInputMap().put(saveKeystroke, "saveKeystroke");
        editorPane.getActionMap().put("saveKeystroke", new AbstractAction() {
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

        editorPane.getInputMap().put(undoKeystroke, "undoKeystroke");
        editorPane.getActionMap().put("undoKeystroke", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UndoManager undoManager = activeSession.getManager();
                try {
                    if (undoManager.canUndo()) undoManager.undo();
                } catch (CannotUndoException ignore) {}
            }
        });

        editorPane.getInputMap().put(redoKeystroke, "redoKeystroke");
        editorPane.getActionMap().put("redoKeystroke", new AbstractAction() {
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
            editorPane.getDocument().removeUndoableEditListener(activeSession.getManager());
            updateSession();
        }

        activeSession = getOrCreateSession(file);

        editorPane.setText(activeSession.getText());
        editorPane.getDocument().addUndoableEditListener(activeSession.getManager());

        //Setting value on main thread causes it to revert to other value
        SwingUtilities.invokeLater(() -> {
            editorScrollPane.getVerticalScrollBar().setValue(activeSession.getVerticalScrollValue());
        });
    }

    private void updateSession() {
        activeSession.setText(editorPane.getText());
        activeSession.setVerticalScrollValue(editorScrollPane.getVerticalScrollBar().getValue());
    }
}
