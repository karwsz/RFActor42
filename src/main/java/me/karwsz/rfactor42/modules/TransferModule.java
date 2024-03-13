package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.objects.SFTPCredentials;
import org.apache.commons.vfs2.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public class TransferModule {

    public TransferModule() {
        if (Application.applicationParams.containsKey("addcredentials")) {
            this.credentials.addAll((Collection<? extends SFTPCredentials>) Application.applicationParams.get("addcredentials"));
        }
    }

    ArrayList<SFTPCredentials> credentials = new ArrayList<>();

    public static void packAndSend(SFTPCredentials credentials, String remoteFile) {
        File file = RFAModule.getOutputFile();
        RFAModule.pack(true, () -> {
            send(credentials, file, remoteFile);
        });
    }

    private static void send(SFTPCredentials credentials, File local, String remotePath) {
        try {
            FileSystemManager fsm = VFS.getManager();
            FileObject localFile = fsm.resolveFile(local.toURI());
            FileObject remoteFile = fsm.resolveFile(
                    URI.create(
                            "sftp://" + credentials.user() + ":" + credentials.password() + "@" + credentials.host() + ":" + credentials.port() + "/" + remotePath
                    )
            );

            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);


        } catch (
                FileSystemException e) {
            throw new RuntimeException(e);
        }
    }

    public void addCredentials(Collection<SFTPCredentials> credentials) {
        this.credentials.addAll(credentials);
    }

    public static class CredentialsManagerGUI extends JFrame {

        private static CredentialsManagerGUI instance;
        private JPanel credentialsList;

        public static CredentialsManagerGUI get() {
            if (instance == null) instance = new CredentialsManagerGUI();
            return instance;
        }

        private CredentialsManagerGUI() {
            init();
        }

        private void init() {
            setTitle(Application.localized("addcredentials"));
            setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
            setLocationRelativeTo(Application.instance);

            JPanel credentialsList = createCredentialsList();
            JScrollPane credentialsScroll = new JScrollPane();
            credentialsScroll.setPreferredSize(credentialsList.getPreferredSize());
            add(credentialsScroll);


            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout());

            JButton addCredentialsButton = new JButton(Application.localized("add"));
            addCredentialsButton.setPreferredSize(new Dimension(100, 50));
            addCredentialsButton.addActionListener(e -> {
                JTextField host = new JTextField();
                JSpinner port = new JSpinner();
                port.setValue(22);
                JTextField user = new JTextField();
                JPasswordField password = new JPasswordField();
                JComponent[] inputs = new JComponent[]{
                        new JLabel(Application.localized("host")), host,
                        new JLabel(Application.localized("port")), port,
                        new JLabel(Application.localized("username")), user,
                        new JLabel(Application.localized("password")), password
                };
                int result = JOptionPane.showConfirmDialog(Application.instance, inputs, "New credentials", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    SFTPCredentials newCredentials = new SFTPCredentials(host.getText(), (Integer) port.getValue(), user.getText(), new String(password.getPassword()));
                    getInstance().addCredentials(newCredentials);
                    Application.globalSettings.addValue("addCredentials", newCredentials.serialize());
                    updateItems();
                }
            });

            JButton editCredentialsButton = new JButton(Application.localized("edit"));
            editCredentialsButton.setPreferredSize(new Dimension(100, 50));

            JButton removeCredentialsButton = new JButton(Application.localized("remove"));
            removeCredentialsButton.setPreferredSize(new Dimension(100, 50));

            buttonsPanel.add(addCredentialsButton);
            buttonsPanel.add(editCredentialsButton);
            buttonsPanel.add(removeCredentialsButton);

            add(buttonsPanel);

            setResizable(false);

            pack();
        }

        private JPanel createCredentialsList() {
            credentialsList = new JPanel();
            credentialsList.setLayout(new BoxLayout(credentialsList, BoxLayout.Y_AXIS));
            credentialsList.setPreferredSize(new Dimension(300, 400));
            credentialsList.setVisible(true);
            return credentialsList;
        }

        SFTPCredentials activeCredentials;
        JLabel activeCredItem;

        public void updateItems() {
            createCredentialsList().removeAll();
            for (SFTPCredentials sftpCredentials : getInstance().credentials) {
                JLabel label = new JLabel(sftpCredentials.user() + "@" + sftpCredentials.host() + ":" + sftpCredentials.port());
                label.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!activeCredItem.equals(label)) {
                            label.setBackground(getBackground().darker());
                        }
                        if (activeCredItem != null) {
                            activeCredItem.setBackground(activeCredItem.getBackground().brighter());
                        }
                        activeCredItem = label;
                        activeCredentials = sftpCredentials;
                    }
                });
                credentialsList.add(label);
                label.setVisible(true);
            }
        }

        @Override
        public void setVisible(boolean b) {
            updateItems();
            super.setVisible(b);
        }
    }

    private void addCredentials(SFTPCredentials newCredentials) {
        if (credentials.stream().anyMatch(sftpCredentials -> sftpCredentials.toString().equalsIgnoreCase(newCredentials.toString()))) return;
        credentials.add(newCredentials);
    }


    private static TransferModule getInstance() {
        return Application.instance.moduleManager.transferModule;
    }
}
