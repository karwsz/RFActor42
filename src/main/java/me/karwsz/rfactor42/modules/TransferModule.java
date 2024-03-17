package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.debug.ExceptionWindow;
import me.karwsz.rfactor42.objects.ProjectSettings;
import me.karwsz.rfactor42.objects.SFTPCredentials;
import org.apache.commons.vfs2.*;
import org.apache.commons.vfs2.provider.ftps.FtpsFileSystemConfigBuilder;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystem;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public class TransferModule {
    ArrayList<SFTPCredentials> credentials = new ArrayList<>();

    public TransferModule() {
        if (Application.applicationParams.containsKey("addcredentials")) {
            for (SFTPCredentials credentials : (Collection<? extends SFTPCredentials>) Application.applicationParams.get("addcredentials")) {
                addCredentials(credentials);
            }
        }
    }

    public static void packAndSend(SFTPCredentials credentials, String remoteFile) {
        File file = RFAModule.getOutputFile();
        RFAModule.pack(true, ProjectSettings.instance().shouldRemoveNonServer(), () -> {
            send(credentials, file, remoteFile);
            JOptionPane.showMessageDialog(Application.instance, Application.localized("transferDone"), "", JOptionPane.PLAIN_MESSAGE);
        });
    }

    private static void send(SFTPCredentials credentials, File local, String remotePath) {
        try {
            FileSystemOptions fileSystemOptions = new FileSystemOptions();
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(fileSystemOptions, false);
            FileSystemManager fsm = VFS.getManager();
            FileObject localFile = fsm.resolveFile(local.toURI());
            FileObject remoteFile = fsm.resolveFile(
                    URI.create(
                            "sftp://" + credentials.user() + ":" + credentials.password() + "@" + credentials.host() + ":" + credentials.port() + "/" + remotePath
                    ).toString(), fileSystemOptions
            );

            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
        } catch (
                FileSystemException e) {
            new ExceptionWindow(e);
            throw new RuntimeException(e);
        }
    }

    private void addCredentials(SFTPCredentials newCredentials) {
        if (credentials.stream().anyMatch(sftpCredentials -> sftpCredentials.toString().equalsIgnoreCase(newCredentials.toString()))) return;
        credentials.add(newCredentials);
    }

    private static TransferModule getInstance() {
        return Application.instance.moduleManager.transferModule;
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

            createCredentialsList();
            JScrollPane credentialsScroll = new JScrollPane(credentialsList);
            credentialsScroll.setPreferredSize(credentialsList.getPreferredSize());
            add(credentialsScroll);


            JPanel buttonsPanel = new JPanel();
            buttonsPanel.setLayout(new FlowLayout());

            JButton addCredentialsButton = new JButton(Application.localized("add"));
            addCredentialsButton.setPreferredSize(new Dimension(100, 50));

            setDefaultCloseOperation(HIDE_ON_CLOSE);


            // ===== FIELDS =====
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
                    if (host.getText().isBlank() || user.getText().isBlank()) return;
                    SFTPCredentials newCredentials = new SFTPCredentials(host.getText(), (Integer) port.getValue(), user.getText(), new String(password.getPassword()));
                    getInstance().addCredentials(newCredentials);
                    Application.globalSettings.addValue("addCredentials", newCredentials.serialize());
                    updateItems();
                }
            });

            JButton editCredentialsButton = new JButton(Application.localized("edit"));
            editCredentialsButton.setPreferredSize(new Dimension(100, 50));

            JButton removeCredentialsButton = new JButton(Application.localized("remove"));
            removeCredentialsButton.addActionListener((e) -> {
                if (activeCredItem != null) {
                    TransferModule.getInstance().credentials.remove(activeCredentials);
                    if (activeCredentials.toString().equals(ProjectSettings.instance().getSelectedHost())) {
                        ProjectSettings.instance().setSelectedHost(null);
                    }
                    Application.globalSettings.removeValue("addCredentials", activeCredentials.serialize());
                    activeCredentials = null;
                    activeCredItem = null;
                    updateItems();
                }
            });
            removeCredentialsButton.setPreferredSize(new Dimension(100, 50));

            buttonsPanel.add(addCredentialsButton);
            buttonsPanel.add(editCredentialsButton);
            buttonsPanel.add(removeCredentialsButton);

            add(buttonsPanel);

            JPanel actionsPanel = new JPanel();
            actionsPanel.setLayout(new FlowLayout());
            JButton sendButton = new JButton(Application.localized("send"));
            sendButton.setPreferredSize(new Dimension(300, 50));
            sendButton.addActionListener(e -> {
                showSendGUI();
            });
            actionsPanel.add(sendButton);

            add(actionsPanel);

            setResizable(false);
            pack();

            setLocationRelativeTo(null);
            Application.centerWindow(this);

            activeCredentials = SFTPCredentials.deserialize(ProjectSettings.instance().getSelectedHost());
        }

        private void createCredentialsList() {
            credentialsList = new JPanel();
            credentialsList.setLayout(new BoxLayout(credentialsList, BoxLayout.Y_AXIS));
            credentialsList.setBorder(new EmptyBorder(new Insets(5, 5, 5, 5)));
            credentialsList.setPreferredSize(new Dimension(300, 400));
            credentialsList.setVisible(true);
        }

        SFTPCredentials activeCredentials;
        CredentialsLabel activeCredItem;

        public void updateItems() {
            credentialsList.removeAll();
            for (SFTPCredentials sftpCredentials : getInstance().credentials) {
                CredentialsLabel label = new CredentialsLabel(sftpCredentials);
                credentialsList.add(label);
                label.setVisible(true);
            }
            revalidate();
            repaint();
        }

        @Override
        public void setVisible(boolean b) {
            updateItems();
            super.setVisible(b);
        }

        private class CredentialsLabel extends JLabel {
            final Color background;
            final Color highlightBackground;
            public CredentialsLabel(SFTPCredentials sftpCredentials) {
                super(sftpCredentials.user() + "@" + sftpCredentials.host() + ":" + sftpCredentials.port());
                setFont(getFont().deriveFont(15f));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new CompoundBorder(new EmptyBorder(new Insets(2, 0, 2, 0)),
                        new LineBorder(UIManager.getColor("Component.borderColor"))));
                setOpaque(true);
                background = getBackground();
                highlightBackground = getBackground().darker();

                if (sftpCredentials.equals(activeCredentials)) setBackground(highlightBackground);
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        if (!CredentialsLabel.this.equals(activeCredItem)) {
                            CredentialsLabel.this.setBackground(highlightBackground);

                            if (activeCredItem != null) {
                                activeCredItem.setBackground(background);
                            }

                            activeCredItem = CredentialsLabel.this;
                            activeCredentials = sftpCredentials;
                            revalidate();
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (!CredentialsLabel.this.equals(activeCredItem)) {
                            CredentialsLabel.this.setBackground(highlightBackground);
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        if (!CredentialsLabel.this.equals(activeCredItem)) {
                            CredentialsLabel.this.setBackground(background);
                        }
                    }
                });
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(credentialsList.getPreferredSize().width, 25);
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        }
    }

    public static void showSendGUI() {
        SFTPCredentials activeCredentials = CredentialsManagerGUI.get().activeCredentials;
        if (activeCredentials == null) {
            JOptionPane.showMessageDialog(null, Application.localized("noCredentialsSelected"), "", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        if (ProjectSettings.instance().getRFABaseDirectory() == null) {
            JOptionPane.showMessageDialog(null, Application.localized("baseDirInstructions"), "", JOptionPane.PLAIN_MESSAGE);
            return;
        }
        String lastTargetFile = ProjectSettings.instance().getLastTargetFile();
        String targetFile = (String) JOptionPane.showInputDialog(null, "Target file location", "", JOptionPane.PLAIN_MESSAGE, null, null, lastTargetFile);
        if (targetFile == null || targetFile.isBlank()) {
            return;
        }
        ProjectSettings.instance().setSelectedHost(activeCredentials);
        ProjectSettings.instance().setLastTargetFile(targetFile);
        packAndSend(activeCredentials, targetFile);
    }


}
