package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.debug.ExceptionWindow;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FileTreeElement {

    FileTreeElement parent;
    private final File file;
    public final int depth;

    public FileTreeElement(FileTreeElement parent, File file) {
        this.parent = parent;
        this.file = file;
        this.depth = parent.depth + 1;
    }

    private FileTreeElement(File file, int depth) {
        this.file = file;
        this.depth = depth;
    }

    public boolean isDirectory() {
        return file.isDirectory();
    }

    public boolean isCONFile() {
        return file.getName().endsWith(".con");
    }

    private final ArrayList<FileTreeElement> children = new ArrayList<>();

    public ArrayList<FileTreeElement> children() {
        return children;
    }

    public File file() {
        return file;
    }


    public static FileTreeElement parent(File file) {
        return new FileTreeElement(file, 0);
    }

    public boolean hasChildren() {
        return !children().isEmpty();
    }


    public FTEIterator iterator() {
        return new FTEIterator();
    }


    /**
     * TODO: optimize
     */
    public class FTEIterator implements Iterator<FileTreeElement> {

        public FTEIterator() {
            this.current = FileTreeElement.this;
        }

        FileTreeElement current;
        Iterator<FileTreeElement> children;

        Iterator<FileTreeElement> childIterator;

        @Override
        public boolean hasNext() {
            return children == null || children.hasNext() || (childIterator != null && childIterator.hasNext());
        }

        @Override
        public FileTreeElement next() {
            if (children == null) {
                children = children().iterator();
                return current;
            }
            else if (childIterator != null) {
                if (!childIterator.hasNext()) {
                    childIterator = null;
                }
                else return childIterator.next();
            }
            if (children.hasNext()) {
                FileTreeElement child = children.next();
                childIterator = child.iterator();
                return childIterator.next();
            }
            throw new NoSuchElementException();
        }
    }

    public String readFully() {
        try {
            return Files.readString(file.toPath());
        } catch (
                IOException e) {
            RuntimeException runtimeException = new RuntimeException(e);
            new ExceptionWindow(runtimeException);
            throw runtimeException;
        }
    }

}
