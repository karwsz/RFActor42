package me.karwsz.rfactor42;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import me.karwsz.rfactor42.debug.ExceptionWindow;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Application extends JFrame {

    public static Application instance;

    public static ResourceBundle localeBundle;

    public Application() {
        setTitle("RFActor42 level editor by Karwsz");
        init();
    }

    protected void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLocationByPlatform(true);
        setMinimumSize(new Dimension(1600, 800));
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        FlatDarculaLaf.setup();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("+")) {
                parseStartupParameter(arg.substring(1), i + 1< args.length ? args[i + 1] : null);
            }
        }

        SwingUtilities.invokeLater(() -> {
            instance = new Application();
        });
    }

    /*
    Parameters list:

    +locale <language> //for example +locale en, +locale pl, +locale de

     */

    private static void parseStartupParameter(String key, String value) {
        if (key.equalsIgnoreCase("locale") && value != null) {
            try {
                localeBundle = PropertyResourceBundle.getBundle("locales/locale", new Locale(value));
            } catch (MissingResourceException e) {
                //If locale is not supported use English
                try {
                    localeBundle = PropertyResourceBundle.getBundle("locales/locale", new Locale("en"));
                } catch (MissingResourceException ignore) {
                    IllegalStateException illegalStateException = new IllegalStateException("No locale found");
                    new ExceptionWindow(illegalStateException);
                    throw illegalStateException;
                }
            }
        }
    }
}
