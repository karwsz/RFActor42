package me.karwsz.rfactor42.modules;


import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;

import java.io.*;


//Possible thanks to henk's scripts
//https://github.com/Ahrkylien/BF1942-Extraction-Readout-Scripts/
public class RFAModule {

    /**
     *
     */
    public static void pack(boolean compress) {
        File parentDir = Application.instance.moduleManager.projectInfo.openDir();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "./python/pack.py", parentDir.getAbsolutePath(), parentDir.getAbsolutePath().toLowerCase().replaceAll(" ", "_") + ".rfa", "" + compress);
            processBuilder.start();
        } catch (IOException e) {
            new ExceptionWindow(e);
            throw new RuntimeException(e);
        }
    }

    /**
     *
     */
    public static void unpack(File file, File outputDir) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "./python/unpack.py", file.getAbsolutePath(), outputDir.getAbsolutePath());
            processBuilder.start();
        } catch (IOException e) {
            new ExceptionWindow(e);
            throw new RuntimeException(e);
        }
    }


}
