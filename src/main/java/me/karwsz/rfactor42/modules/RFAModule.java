package me.karwsz.rfactor42.modules;


import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.objects.ProjectSettings;

import java.io.*;


//Possible thanks to henk's scripts
//https://github.com/Ahrkylien/BF1942-Extraction-Readout-Scripts/
public class RFAModule {

    private static boolean packing = false;

    public static boolean isPacking() {
        return packing;
    }

    public static void pack(boolean compress) {
        ProjectSettings settings = Application.instance.moduleManager.projectSettings;
        File parentDir = Application.instance.moduleManager.projectSettings.parentDir();
        try {
            File outputFile = new File(parentDir.getAbsolutePath() + File.separator + parentDir.getName().toLowerCase().replaceAll(" ", "_") + ".rfa");
            outputFile.createNewFile();
            ProcessBuilder processBuilder = new ProcessBuilder("python", "./python/pack.py", parentDir.getAbsolutePath(), outputFile.getAbsolutePath(),
                    settings.getRFABaseDirectory().getAbsolutePath(),
                    "" + compress);
            Process process = processBuilder.start();
            packing = true;
            new Thread(() -> {
                try {
                    process.waitFor();
                } catch (
                        InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    String errors = new String(process.getErrorStream().readAllBytes());
                    System.out.println(errors);
                } catch (
                        IOException e) {
                    throw new RuntimeException(e);
                }

                packing = false;
            }).start();

        } catch (IOException e) {
            new ExceptionWindow(e);
            throw new RuntimeException(e);
        }
    }

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
