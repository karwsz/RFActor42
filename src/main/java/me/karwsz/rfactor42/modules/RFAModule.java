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
            // ===== SETUP =====
            ProcessBuilder processBuilder = new ProcessBuilder("python", "./python/pack.py", parentDir.getAbsolutePath(), parentDir.getAbsolutePath() + ".rfa", "" + compress);
            Process p = processBuilder.start();

//            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));

        } catch (IOException e) {
            new ExceptionWindow(e);
            throw new RuntimeException(e);
        }
    }
}
