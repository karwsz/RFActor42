package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.Application;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GlobalSettings {
    public final HashMap<String, String> settings = new HashMap<>();

    public void parseFromFile() {
        File file = new File("./global.r42");
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
        settings.putIfAbsent("theme", "Darcula");
        settings.putIfAbsent("locale", "en");
        write();
    }

    public void write() {
        File settingsFile = new File("./global.r42");
        try {
            settingsFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fileWriter = new FileWriter(settingsFile)) {
            for (Map.Entry<String, String> entry : settings.entrySet()) {
                fileWriter.append("+").append(entry.getKey()).append(" ").append(entry.getValue() != null ? entry.getValue() : "").append("\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
