package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.objects.FileTreeElement;

import javax.swing.undo.UndoManager;
import java.io.*;

public class EditorSession {

    private final UndoManager manager;
    private final FileTreeElement element;
    private String text;

    public EditorSession(FileTreeElement file) {
        this.element = file;
        this.manager = new UndoManager();
        this.text = file.readFully();
    }

    public UndoManager getManager() {
        return manager;
    }

    public FileTreeElement getElement() {
        return element;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void write() {
        File file = element.file();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(text);
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
    }
}
