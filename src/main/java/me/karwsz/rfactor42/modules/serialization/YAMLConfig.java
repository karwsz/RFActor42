package me.karwsz.rfactor42.modules.serialization;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class YAMLConfig {

    private HashMap<String, Object> data;

    public YAMLConfig() {

    }

    public void load(File file) throws FileNotFoundException {
        data = new Yaml().load(new FileInputStream(file));
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

    private String lastKey(String path) {
        if (path.contains(".")) {
            String[] keys = path.split("\\.");
            return keys[keys.length - 1];
        }
        return path;
    }

    public void set(String path, Object value) {
        Map<String, Object> data = createPath(path);
        data.put(lastKey(path), value);
    }

    public String getString(String path) {
        return getString(path, null);
    }

    public String getString(String path, String def) {
        return (String) getObject(path, def);
    }

    private Object getObject(String path, String def) {
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> resolvePath(String path) {
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
        while (i < keys.length) {
            Map<String, Object> next = new LinkedHashMap<>();
            data.put(keys[i], next);
            data = next;
            i++;
        }
        return data;
    }



}
