package org.j5mclaunch.launcher.http;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class HackyHostnameVerifier implements HostnameVerifier {
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
