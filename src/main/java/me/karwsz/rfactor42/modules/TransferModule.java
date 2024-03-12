package me.karwsz.rfactor42.modules;

import me.karwsz.rfactor42.Application;
import me.karwsz.rfactor42.objects.SFTPCredentials;
import org.apache.commons.vfs2.*;

import javax.swing.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;

public class TransferModule {

    public TransferModule() {
        if (Application.applicationParams.containsKey("credentials")) {
            this.credentials.addAll((Collection<? extends SFTPCredentials>) Application.applicationParams.get("credentials"));
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

        public CredentialsManagerGUI() {

        }

    }
}
