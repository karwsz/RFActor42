package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.modules.RFAModule;
import me.karwsz.rfactor42.modules.serialization.YAMLConfig;

import java.io.*;

public class ProjectSettings extends YAMLConfig {
    private final File parentDir;
    private File baseDirectory = null;

    private boolean showConFilesOnly = true;
    private String lastTargetFile;
    private String selectedHost;
    private boolean shouldRemoveNonServer;

    public ProjectSettings(File parentDir) {
        this.parentDir = parentDir;
    }


    private boolean compress = true;

    public void setCompress(boolean compress) {
        this.compress = compress;
        save(getProjectSettingsFile());
    }

    public boolean shouldCompress() {
        return compress;
    }

    public File parentDir() {
        return parentDir;
    }

    public File getRFABaseDirectory() {
        return baseDirectory;
    }

    public void setRFABaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
        save(getProjectSettingsFile());
    }

    public static ProjectSettings instance() {
        return Application.instance.moduleManager.projectSettings;
    }


    public void init() {
        File psf = getProjectSettingsFile();
        try {
            load(psf);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.baseDirectory = getFile("RFAbase");
        this.compress = getBoolean("compress", true);
        this.showConFilesOnly = getBoolean("showConFilesOnly", true);
        this.lastTargetFile = getString("lastFileTarget",
                "/home/example/bf1942/mods/bf1942/archives/bf1942/levels/" + RFAModule.getOutputFile().getName());
       this.selectedHost = getString("selectedHost", null);

        if (!Application.globalSettings.getValues("selectedHost").contains(selectedHost)) {
            this.selectedHost = null;
        }

        this.shouldRemoveNonServer = getBoolean("removeNonServer", false);

        save(psf);
    }



    private File getProjectSettingsFile() {
        File file = new File(parentDir, PROJECT_SETTINGS_FILE);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
    public static String PROJECT_SETTINGS_FILE = "project.r42";




    public void toggleShowConFilesOnly() {
        showConFilesOnly = !showConFilesOnly;
        save(getProjectSettingsFile());
    }

    public boolean shouldShowConFilesOnly() {
        return showConFilesOnly;
    }

    public String getLastTargetFile() {
        return lastTargetFile;
    }

    public void setLastTargetFile(String lastTargetFile) {
        this.lastTargetFile = lastTargetFile;
        save(getProjectSettingsFile());
    }

    public String getSelectedHost() {
        return selectedHost;
    }

    public void setSelectedHost(SFTPCredentials selectedHost) {
        this.selectedHost = selectedHost.serialize();
        save(getProjectSettingsFile());
    }

    public void setRemoveNonServer(boolean state) {
        this.shouldRemoveNonServer = state;
    }

    public boolean shouldRemoveNonServer() {
        return shouldRemoveNonServer;
    }
}
