package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.Application;

import javax.swing.*;
import java.io.File;

public class ProjectSettings {
    private File parentDir;
    private FileComponent baseDirectory;

    public ProjectSettings(File parentDir) {
        this.parentDir = parentDir;
    }


    private boolean compress = true;

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean shouldCompress() {
        return compress;
    }

    public File parentDir() {
        return parentDir;
    }

    public void setParentDir(File parentDir) {
        this.parentDir = parentDir;
    }

    public FileComponent getRFABaseDirectory() {
        return baseDirectory;
    }

    public void setRFABaseDirectory(FileComponent baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public static ProjectSettings instance() {
        return Application.instance.moduleManager.projectSettings;
    }

}
