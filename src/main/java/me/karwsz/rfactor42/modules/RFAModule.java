package me.karwsz.rfactor42.modules;


import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.objects.ProjectSettings;

import java.io.*;


//Possible thanks to henk's scripts
//https://github.com/Ahrkylien/BF1942-Extraction-Readout-Scripts/

//TODO: add remove server files option
public class RFAModule {

    private static boolean packing = false;

    public static boolean isPacking() {
        return packing;
    }

    public static File getOutputFile() {
        File parentDir = Application.instance.moduleManager.projectSettings.parentDir();
        return new File(parentDir.getAbsolutePath() + File.separator + parentDir.getName().toLowerCase().replaceAll(" ", "_") + ".rfa");
    }

    public static void pack(boolean compress) {
        pack(compress, null);
    }

    public static void pack(boolean compress, Runnable then) {
        ProjectSettings settings = Application.instance.moduleManager.projectSettings;
        try {
            File outputFile = getOutputFile();
            outputFile.createNewFile();
            ProcessBuilder processBuilder = new ProcessBuilder("python", "./python/pack.py", ProjectSettings.instance().parentDir().getAbsolutePath(), outputFile.getAbsolutePath(),
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
                    if (then != null && process.exitValue() == 0) {
                        then.run();
                    }
                    String errors = new String(process.getErrorStream().readAllBytes());
                    System.out.println(errors);
                } catch (
                        IOException e) {
                    packing = false;
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
