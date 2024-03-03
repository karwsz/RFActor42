package me.karwsz.rfactor42.debug;

import me.karwsz.rfactor42.Application;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.net.URISyntaxException;

public class ExceptionWindow extends JDialog {

    public ExceptionWindow(Exception exception) {
        init(exception);
    }

    protected void init(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        String stackTrace = sw.toString();

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setTitle("Exception log");
        JTextArea textArea = new JTextArea();
        textArea.setBorder(new CompoundBorder(
                new EmptyBorder(new Insets(10, 20, 10, 20))
        ,  new LineBorder(Color.BLACK)));
        textArea.setText(stackTrace);
        textArea.setEditable(false);
        add(new JScrollPane(textArea));
        JLabel contactMe = new JLabel();
        contactMe.setPreferredSize(new Dimension(0, 20));
        contactMe.setText("Click here to get contact details");
        contactMe.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    File contactMe = new File(Application.class.getResource("/contact.txt").toURI());
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(contactMe));
                    JTextArea textComponent = new JTextArea();
                    textComponent.setFont(textComponent.getFont().deriveFont(16f));
                    textComponent.setOpaque(false);
                    textComponent.setText(new String(bufferedInputStream.readAllBytes()));
                    JOptionPane.showMessageDialog(Application.instance, textComponent, "Contact me", JOptionPane.INFORMATION_MESSAGE);

                } catch (
                        URISyntaxException ex) {
                    new ExceptionWindow(ex);
                } catch (
                        IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        contactMe.setHorizontalTextPosition(SwingConstants.CENTER);
        add(contactMe);
        pack();
        setVisible(true);
    }

}
