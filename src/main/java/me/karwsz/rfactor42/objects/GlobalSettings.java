package me.karwsz.rfactor42.objects;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//TODO: Rewrite to objective, both for local and global settings
public class GlobalSettings {
    private final HashMap<String, ArrayList<String>> settings = new HashMap<>();

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
                    addValue(key, keyVal.length < 2 || "".equalsIgnoreCase(keyVal[1]) ? null : line.substring(key.length() + 2));
                }
            }
        } catch (
                IOException e) {
            throw new RuntimeException(e);
        }
        addValue("theme", "Darcula");
        addValue("locale", "en");
        write();
    }

    public void addValue(@NotNull String key, String value) {
        key = key.toLowerCase();
        if (value == null) {
            if ((getFlags(key) & StoreFlags.ALLOW_NULL.getValue()) != StoreFlags.ALLOW_NULL.getValue()) {
                return;
            }
        }
        settings.putIfAbsent(key, new ArrayList<>());
        ArrayList<String> values = settings.get(key);
        StoreStrategy strategy = getStrategy(key);
        if (strategy.equals(StoreStrategy.ALLOW_ONE)) {
            values.clear();
        }
        else if (strategy.equals(StoreStrategy.REMOVE_DUPLICATES)) {
            if (values.contains(value)) return;
        }
        values.add(value);
        write();
    }

    private ArrayList<String> getValues(String key) {
        return settings.getOrDefault(key, new ArrayList<>());
    }

    public void write() {
        File settingsFile = new File("./global.r42");
        try {
            settingsFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (FileWriter fileWriter = new FileWriter(settingsFile)) {
            for (Map.Entry<String, ArrayList<String>> entry : settings.entrySet()) {
                String key = entry.getKey();
                ArrayList<String> values = entry.getValue();
                for (String value : values) {
                    fileWriter.append("+").append(key).append(" ").append(value != null ? value : "").append("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final HashMap<String, StoreStrategy> keyStrategies = new HashMap<>();
    private static final HashMap<String, Integer> keyFlags = new HashMap<>();
    static {
        keyStrategies.put("open", StoreStrategy.ALLOW_ONE);
        keyStrategies.put("addcredentials", StoreStrategy.REMOVE_DUPLICATES);
    }

    private StoreStrategy getStrategy(String key) {
        return keyStrategies.getOrDefault(key, StoreStrategy.REMOVE_DUPLICATES);
    }
    private int getFlags(String key) {
        return keyFlags.getOrDefault(key, 0);
    }

    public Iterable<? extends Map.Entry<String, ArrayList<String>>> getSettings() {
        return settings.entrySet();
    }

    enum StoreFlags {
        ALLOW_NULL(0x00000001)
        ;
        final int value;
        StoreFlags(int val) {
            this.value = val;
        }

        public int getValue() {
            return value;
        }
    }

    enum StoreStrategy {
        ALLOW_ONE, REMOVE_DUPLICATES;
    }

}
