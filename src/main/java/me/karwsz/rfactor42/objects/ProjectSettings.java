package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.Application;

import java.io.*;
import java.util.HashMap;

public class ProjectSettings {
    private File parentDir;
    private File baseDirectory = null;

    private boolean showConFilesOnly = true;

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
            fileWriter.append("+showConFilesOnly").append(" ").append(((Boolean) showConFilesOnly).toString()).append("\n");
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
}
