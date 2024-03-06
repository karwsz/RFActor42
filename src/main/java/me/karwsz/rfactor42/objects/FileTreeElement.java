package me.karwsz.rfactor42.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public record FileTreeElement(
        File file,
        int depth, ArrayList<FileTreeElement> children) {
    public static FileTreeElement parent(File file) {
        return new FileTreeElement(file, 0, new ArrayList<>());
    }

    public String displayString() {
        StringBuilder builder = new StringBuilder();
        builder.append("--".repeat(depth * 2 + 2));
        if (file().isFile())
            builder.append("f");
        else
            builder.append("+");
        return builder.toString();
    }


    public static class FTEIterator implements Iterator<FileTreeElement> {

        public FTEIterator(FileTreeElement parent) {
            this.current = parent;
        }

        FileTreeElement current;

        Iterator<FileTreeElement> children;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public FileTreeElement next() {

        }
    }
}
