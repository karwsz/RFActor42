package me.karwsz.rfactor42.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class FileTreeElement {

    private final File file;
    public final int depth;

    public FileTreeElement(FileTreeElement parent, File file) {
        this.file = file;
        this.depth = parent.depth + 1;
    }

    private FileTreeElement(File file, int depth) {
        this.file = file;
        this.depth = depth;
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

    public String displayString() {
        StringBuilder builder = new StringBuilder();
        builder.append("--".repeat(depth + 1));
        if (file().isFile())
            builder.append("f").append(file.getName());
        else
            builder.append("+").append(file.getName());
        return builder.toString();
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

    public static void testString() {
        FileTreeElement parent = FileTreeElement.parent(new File("Test"));
        for (int i = 0; i < 10; i++) {
            FileTreeElement child = new FileTreeElement(parent, new File("" + i));
            parent.children().add(child);
            for (int j = 0; j < 3; j++) {
                FileTreeElement grandchild = new FileTreeElement(child, new File("" + j));
                child.children().add(grandchild);
            }
        }
        for (FTEIterator it = parent.iterator(); it.hasNext(); ) {
            FileTreeElement fte = it.next();
            System.out.println(fte.displayString());
        }
    }
}
