package me.karwsz.rfactor42;

import me.karwsz.rfactor42.debug.ExceptionWindow;

import javax.swing.*;

public class Application extends JFrame {

    public static Application instance;

    public Application() {
        setTitle("RFActor42 level editor by Karwsz");
        init();
    }

    private void init() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (
                ClassNotFoundException |
                InstantiationException |
                IllegalAccessException |
                UnsupportedLookAndFeelException e) {
            new ExceptionWindow(e);
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLocationByPlatform(true);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            instance = new Application();
        });
    }
}
