package au.edu.bond.classgroups.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by shane on 14/09/2015.
 */
public class ServerUtil {

    public static String getServerName() {
        String server = null;
        try {
            server = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return server;
    }

}
