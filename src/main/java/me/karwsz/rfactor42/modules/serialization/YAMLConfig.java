package me.karwsz.rfactor42.modules.serialization;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class YAMLConfig {

    private final HashMap<String, Object> data = new LinkedHashMap<>();

    public YAMLConfig() {

    }

    public void load(File file) throws FileNotFoundException {
        data.clear();
        data.putAll(new Yaml().load(new FileInputStream(file)));
    }

    public void load(String string) {
        data.clear();
        data.putAll(new Yaml().load(string));
    }

    public void save(File file) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        try {
            new Yaml(dumperOptions).dump(data, new FileWriter(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String dump() {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(dumperOptions).dump(data);
    }

    public static void validatePath(String path) {
        if (path == null || path.isBlank()
        || path.startsWith(".") || path.endsWith(".")) {
            throw new IllegalArgumentException("Path is invalid: '" + path + "'");
        }
    }

    public boolean containsKey(String path) {
        validatePath(path);
        String[] keys = path.split("\\.");
        int i = 0;
        Map<String, Object> data = this.data;
        while (i < keys.length - 1) {
            if (!data.containsKey(keys[i])) {
                return false;
            }
            try {
                data = (Map<String, Object>) data.get(keys[i]);
            } catch (ClassCastException | NullPointerException e) {
                return false;
            }
            i++;
        }
        return true;
    }

    @SuppressWarnings("all")
    private String pathToKey(String path) {
        validatePath(path);
        if (path.contains(".")) {
            String key = "";
            for (int i = path.length() - 1; i >= 0; i--) {
                char c = path.charAt(i);
                if (c == '.') break;
                key = c + key;
            }
            return key;
        }
        return path;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolvePath(String path) {
        validatePath(path);
        String[] keys = path.split("\\.");
        int i = 0;
        Map<String, Object> data = this.data;
        while (i < keys.length - 1) {
            try {
                data = (Map<String, Object>) data.get(keys[i]);
            } catch (ClassCastException | NullPointerException e) {
                throw new NoSuchElementException();
            }
            i++;
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> createPath(String path) {
        validatePath(path);
        String[] keys = path.contains(".")  ? path.split("\\.") : new String[]{path};
        int i = 0;
        Map<String, Object> data = this.data;
        while (i < keys.length - 1) {
            Object next;
            if (!data.containsKey(keys[i])) {
                return createPathUnobstructed(data, keys, i);
            }
            else {
                next = data.get(keys[i]);
                if (!(next instanceof Map)) {
                    return createPathUnobstructed(data, keys, i);
                }
                data = (Map<String, Object>) next;
            }
            i++;
        }
        return data;
    }

    private Map<String, Object> createPathUnobstructed(Map<String, Object> data, String[] keys, int i) {
        while (i < keys.length - 1) {
            Map<String, Object> next = new LinkedHashMap<>();
            data.put(keys[i], next);
            data = next;
            i++;
        }
        return data;
    }


    public void set(String path, Object value) {
        Map<String, Object> data = createPath(path);
        data.put(pathToKey(path), value);
    }

    public String getString(String path) {
        return getString(path, null);
    }

    public String getString(String path, String def) {
        return (String) getObject(path, def);
    }

    public boolean getBoolean(String path) {
        return getBoolean(path, null);
    }

    public boolean getBoolean(String path, Boolean def) {
        return Boolean.parseBoolean(getString(path, "" + def));
    }

    public File getFile(String path) {
        return getFile(path, null);
    }

    public File getFile(String path, File def) {
        return (File) getObject(path, def);
    }


    public Object getObject(String path) {
        validatePath(path);
        return getObject(path, null);
    }

    public Object getObject(String path, Object def) {
        validatePath(path);
        return resolvePath(path).getOrDefault(pathToKey(path), def);
    }
}
