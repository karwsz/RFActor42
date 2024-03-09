package me.karwsz.rfactor42;

import com.formdev.flatlaf.FlatDarculaLaf;
import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.modules.ModuleManager;
import me.karwsz.rfactor42.modules.RFActorMenuBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class Application extends JFrame {

    /*
    Parameters list:

    +locale <language> //for example +locale en, +locale pl, +locale de
    +theme Darcula/IntellIJ/Light/Dark //CASE SENSITIVE
     */

    private static void parseStartupParameter(String key, String value) {
        switch (key.toLowerCase()) {
            case "locale" -> {
                if (value == null) return;
                try {
                    loc = PropertyResourceBundle.getBundle("locales/locale", new Locale(value));
                } catch (MissingResourceException e) {
                    //If locale is not supported use English
                    try {
                        loc = PropertyResourceBundle.getBundle("locales/locale", new Locale("en"));
                    } catch (MissingResourceException ignore) {
                        IllegalStateException illegalStateException = new IllegalStateException("No locale found");
                        new ExceptionWindow(illegalStateException);
                        throw illegalStateException;
                    }
                }
            }
            case "theme" -> {
                if (value == null) return;
                String className = "com.formdev.flatlaf.Flat" + value + "Laf";
                try {
                    UIManager.setLookAndFeel(className);
                } catch (
                        ClassNotFoundException |
                        InstantiationException |
                        IllegalAccessException |
                        UnsupportedLookAndFeelException ignore) {
                }
            }
        }
    }

    public static Application instance;
    private static ResourceBundle loc;

    public ModuleManager moduleManager;



    public Application() {
        System.setProperty("awt.useSystemAAFontSettings","on");
        setTitle("RFActor42 level editor by Karwsz");
        init();
    }

    protected void init() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLocationByPlatform(true);
        setPreferredSize(new Dimension(1600, 800));

        JPanel contentPane = new JPanel();
        contentPane.setOpaque(true);
        contentPane.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        setContentPane(contentPane);

        setJMenuBar(new RFActorMenuBar());

        initComponents();

        pack();
        setVisible(true);
    }

    private void initComponents() {
        this.moduleManager = new ModuleManager();
        this.moduleManager.attach(this.getContentPane());
    }

    public static void main(String[] args) {

        //Defaults
        FlatDarculaLaf.setup();
        parseStartupParameter("locale", "en");


        //Parse args
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

    /**
     *
     * @param key - key of the resource
     * @return localized string, if present in property file
     * @throws MissingResourceException if key is not present
     * (and informs user through ExceptionWindow)
     */
    public static String localized(String key) {
        try {
            return Application.loc.getString(key);
        } catch (MissingResourceException e) {
            new ExceptionWindow(e);
            throw e;
        }
    }

}
