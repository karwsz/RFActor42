package me.karwsz.rfactor42.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record SFTPCredentials(String host, int port, String user, String password) {


    public String serialize() {
        return "\"" + host + "\" " + port + " \"" + user + "\" \"" + password + "\"";
    }

    public static SFTPCredentials deserialize(String string) {
        if (string == null) return null;
        Pattern pattern = Pattern.compile("^\"(.*?)\" ([0-9].*?) \"(.*?)\" \"(.*?)\"$");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.find()) {
            return null;
        }
        String host = matcher.group(1);
        int port = Integer.parseInt(matcher.group(2));
        String user = matcher.group(3);
        String password = matcher.group(4);
        return new SFTPCredentials(host, port, user, password);
    }


}
