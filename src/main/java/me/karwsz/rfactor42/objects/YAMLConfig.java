package me.karwsz.rfactor42.objects;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class YAMLConfig {

    private Yaml data;
    public YAMLConfig() {
        data = new Yaml();
    }

    public void load(File file) {
        try {
            this.data.load(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
