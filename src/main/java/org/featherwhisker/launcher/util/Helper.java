package org.featherwhisker.launcher.util;

import org.featherwhisker.launcher.http.CurlHttpClient;
import org.featherwhisker.launcher.http.HttpClient;
import org.featherwhisker.launcher.http.JavaHttpClient;

import javax.swing.*;
import java.io.*;
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
                    http = new CurlHttpClient();
                } else {
                    http = new JavaHttpClient();
                }
            } catch (Exception e) {
                http = new JavaHttpClient();
                popup("cURL was not found on your system! This is needed for TLS v1.3 on old systems.");
            }
        }
        return http;
    }
}
