package org.featherwhisker.launcher.util;

import org.featherwhisker.launcher.http.CurlHttpClient;
import org.featherwhisker.launcher.http.HttpClient;
import org.featherwhisker.launcher.http.JavaHttpClient;

import javax.net.ssl.SSLContext;
import javax.swing.*;
import java.io.*;
import java.util.Locale;
import java.util.zip.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.featherwhisker.launcher.Main.setStatus;

public class Helper {
    public static void makeDir(String dir) {
        File dir1 = new File(dir);
        if (!dir1.exists()) dir1.mkdirs();
    }

    public static void printf(String str) {
        System.out.println(str);
        setStatus(str);
    }
    public static void unzip(String zipFile, String destFolder) throws IOException {
        //https://www.geeksforgeeks.org/how-to-zip-and-unzip-files-in-java/
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry entry;
            byte[] buffer = new byte[1024];
            while ((entry = zis.getNextEntry()) != null) {
                File newFile = new File(destFolder + File.separator + entry.getName());
                if (entry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    new File(newFile.getParent()).mkdirs();
                    try {
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    } catch (Exception ex){
                        ex.printStackTrace();
                        error(ex.getMessage());
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
            error(ex.getMessage());
        }
    }
    public static double getJavaVer() {
        return Double.parseDouble(System.getProperty("java.specification.version"));
    }
    public static boolean javaClientSupported() {
        try {
            SSLContext supported = SSLContext.getInstance("TLSv1.3");
            return supported != null;
        } catch(Exception e) {
            return false;
        }
    }
    public static double getOSXVer() {
        String ver = System.getProperty("os.version");
        String[] ver1 = ver.split(".");
        return Double.parseDouble(ver1[0]+"."+ver1[1]);
    }
    public static boolean isOSX() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        return (OS.contains("mac")) || (OS.contains("darwin"));
    }
    public static boolean isWindows() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        return OS.contains("win");
    }
    public static void popup(String str) {
        JOptionPane.showMessageDialog(null, str, "j5mclaunch",
                JOptionPane.INFORMATION_MESSAGE);
    }
    public static void error(String str) {
        JOptionPane.showMessageDialog(null, str, "Error: j5mclaunch",
                JOptionPane.ERROR_MESSAGE);
    }
    private static HttpClient http;
    public static HttpClient getHttpClient() {
        if (http == null) {
            try {
                File f = new File("/usr/local/opt/curl/bin/curl");
                File f1 = new File("/opt/local/bin/curl");
                if (f.exists()) {
                    http = new CurlHttpClient("/usr/local/opt/curl/bin/curl");
                } else if (f1.exists()) {
                    http = new CurlHttpClient("/opt/local/bin/curl");
                } else {
                    Runtime rt = Runtime.getRuntime();
                    Process proc = rt.exec("curl -V");
                    BufferedReader out = new BufferedReader(new
                            InputStreamReader(proc.getInputStream()));
                    String s = "";
                    String s1;
                    while ((s1 = out.readLine()) != null) {
                        s += s1 + "\n";
                    }
                    if (s.contains("libcurl")) {
                        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                        if ((OS.contains("mac")) || (OS.contains("darwin")) && getOSXVer() < 10.15) {
                            if (getOSXVer() > 10.8) {
                                popup("cURL on OS X is as old as the version is. Your version of OS X is too old for TLSv1.3 to be in the bundled cURL. Falling back onto Java http...");
                                http = new JavaHttpClient();
                            } else {
                                error("cURL on OS X is as old as the version is. You need to install a new version with port or brew to play the game.");
                                System.exit(1);
                            }
                        }
                        http = new CurlHttpClient();
                    } else {
                        if (getJavaVer() < 11) {
                            popup("cURL was not found on your system! This is needed for TLS v1.3 on old systems.");
                        }
                        http = new JavaHttpClient();
                    }
                }
            } catch (Exception e) {
                if (getJavaVer() < 11) {
                    popup("cURL was not found on your system! This is needed for TLS v1.3 on old systems.");
                }
                http = new JavaHttpClient();
            }
        }
        return http;
    }
}
