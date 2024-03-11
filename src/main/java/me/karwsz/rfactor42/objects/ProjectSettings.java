package me.karwsz.rfactor42.objects;

import me.karwsz.rfactor42.Application;

public class ProjectSettings {
    public ProjectSettings() {

    }

    private boolean compress = false;

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public boolean shouldCompress() {
        return compress;
    }

}
