package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.modules.RFAModule;

import java.io.*;
import java.util.HashMap;

public class ProjectSettings {
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
        write();
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
        write();
    }

    public static ProjectSettings instance() {
        return Application.instance.moduleManager.projectSettings;
    }


    public void parseFile() {
        File file = new File(parentDir,"project.r42");
        HashMap<String, String> settings = new HashMap<>();
        try {
            if (!file.exists()) file.createNewFile();
            BufferedReader bufferedReader = new BufferedReader(new BufferedReader(new FileReader(file)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("+")) {
                    String[] keyVal = line.split(" ");
                    String key = keyVal[0].substring(1);
                    settings.put(key, keyVal.length < 2 || "".equalsIgnoreCase(keyVal[1]) ? null : line.substring(key.length() + 2));
                }
            }
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }

        String baseDirString = settings.getOrDefault("RFAbase", null);
        this.baseDirectory = baseDirString != null && !baseDirString.isBlank() ? new File(baseDirString) : null;
        this.compress = Boolean.parseBoolean(settings.getOrDefault("compress", "true"));
        this.showConFilesOnly = Boolean.parseBoolean(settings.getOrDefault("showConFilesOnly", "true"));
        String lastTargetFileString = settings.getOrDefault("lastFileTarget", null);
        this.lastTargetFile = lastTargetFileString != null ? lastTargetFileString : "/home/example/bf1942/mods/bf1942/archives/bf1942/levels/" + RFAModule.getOutputFile().getName();
        this.selectedHost = settings.getOrDefault("selectedHost", null);
        this.shouldRemoveNonServer = Boolean.parseBoolean(settings.getOrDefault("removeNonServer", "false"));

        write();
    }

    public void write() {
        File settingsFile = new File(parentDir, "project.r42");
        try {
            settingsFile.createNewFile();
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fileWriter = new FileWriter(settingsFile)) {
            fileWriter.append("+RFAbase").append(" ").append(getRFABaseDirectory() == null ? "" : getRFABaseDirectory().getAbsolutePath()).append("\n");
            fileWriter.append("+compress").append(" ").append(((Boolean) compress).toString()).append("\n");
            fileWriter.append("+removeNonServer").append(" ").append(((Boolean) shouldRemoveNonServer).toString()).append("\n");
            fileWriter.append("+showConFilesOnly").append(" ").append(((Boolean) showConFilesOnly).toString()).append("\n");

            if (lastTargetFile != null) fileWriter.append("+lastFileTarget").append(" ").append((lastTargetFile)).append("\n");
            if (selectedHost != null) fileWriter.append("+selectedHost").append(" ").append((selectedHost)).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    public void toggleShowConFilesOnly() {
        showConFilesOnly = !showConFilesOnly;
        write();
    }

    public boolean shouldShowConFilesOnly() {
        return showConFilesOnly;
    }

    public String getLastTargetFile() {
        return lastTargetFile;
    }

    public void setLastTargetFile(String lastTargetFile) {
        this.lastTargetFile = lastTargetFile;
        write();
    }

    public String getSelectedHost() {
        return selectedHost;
    }

    public void setSelectedHost(SFTPCredentials selectedHost) {
        this.selectedHost = selectedHost.serialize();
        write();
    }

    public void setRemoveNonServer(boolean state) {
        this.shouldRemoveNonServer = state;
    }

    public boolean shouldRemoveNonServer() {
        return shouldRemoveNonServer;
    }
}
