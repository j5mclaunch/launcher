package org.j5mclaunch.launcher.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jsse.provider.BouncyCastleJsseProvider;
import org.j5mclaunch.launcher.http.CurlHttpClient;
import org.j5mclaunch.launcher.http.HackyHostnameVerifier;
import org.j5mclaunch.launcher.http.HttpClient;
import org.j5mclaunch.launcher.http.JavaHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.*;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.Security;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Scanner;
import java.util.zip.*;

import static org.j5mclaunch.launcher.Launcher.setStatus;

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
                        fos.close();
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
    private static boolean fixed = false;
    public static boolean javaClientSupported() {
        if (!fixed) {
            try {
                SSLContext s = SSLContext.getInstance("TLSv1.3");
                return s != null;
            } catch(Exception e) {
                return false;
            }
        }
        return false;
    }
    public static double getOSXVer() {
        String ver = System.getProperty("os.version");
        String[] ver1 = ver.split("\\.");
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
            http = new JavaHttpClient();
        }
        return http;
    }
    public static HttpClient getHttpClientOld() {
        if (http == null) {
            try {
                File f = new File("/usr/local/opt/curl/bin/curl");
                File f1 = new File("/opt/local/bin/curl");
                File f2 = new File(MinecraftLauncher.getMinecraftFolder()+"/curl");
                File f3 = new File(MinecraftLauncher.getMinecraftFolder()+"/curl.exe");
                if (f.exists()) {
                    http = new CurlHttpClient("/usr/local/opt/curl/bin/curl");
                } else if (f1.exists()) {
                    http = new CurlHttpClient("/opt/local/bin/curl");
                } else if (f2.exists()) {
                    http = new CurlHttpClient(MinecraftLauncher.getMinecraftFolder()+"/curl");
                } else if (f3.exists()) {
                    http = new CurlHttpClient(MinecraftLauncher.getMinecraftFolder()+"/curl.exe");
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
                            if (!Helper.javaClientSupported()) {
                                popup("Java authentication will break in the future once TLS1.2 is deprecated. Please install cURL with homebrew if possible");
                            }
                            http = new JavaHttpClient();
                        }else if (s.contains("(Windows)") || s.contains("WinIDN")) {
                            if (!Helper.javaClientSupported()) {
                                popup("Java authentication will break in the future once TLS1.2 is deprecated. Please install a newer Java if possible");
                            }
                            http = new JavaHttpClient();
                        } else {
                            http = new CurlHttpClient();
                        }
                    } else {
                        if (!Helper.javaClientSupported()) {
                            popup("Java authentication will break in the future once TLS1.2 is deprecated. Please install modern cURL if possible");
                        }
                        http = new JavaHttpClient();
                    }
                }
            } catch (Exception e) {
                if (!Helper.javaClientSupported()) {
                    popup("Java authentication will break in the future once TLS1.2 is deprecated. Please install modern cURL if possible");
                }
                http = new JavaHttpClient();
            }
        }
        return http;
    }
        private static SSLContext sslcon;
        private static TrustManagerFactory tmf;
        private static HostnameVerifier h = new HackyHostnameVerifier();
        public static void fixSecurity(HttpsURLConnection con) {
            if (!Helper.javaClientSupported()) {
                con.setSSLSocketFactory(sslcon.getSocketFactory());
                con.setHostnameVerifier(h);
            }
        }
        public static void fixHttpSecurity() {
            fixed = true;
            System.setProperty("org.bouncycastle.jsse.client.assumeOriginalHostName","true");
            System.setProperty("org.bouncycastle.jsse.client.acceptRenegotiation","true");
            System.setProperty("sun.security.ssl.allowLegacyHelloMessages","true");
            System.setProperty("sun.security.ssl.allowUnsafeRenegotiation","true");
            Security.insertProviderAt(new BouncyCastleProvider(),1);
            Security.insertProviderAt(new BouncyCastleJsseProvider(),2);
            try {
                KeyStore k = KeyStore.getInstance("bks","BC");
                k.load(Helper.class.getResourceAsStream("/cacert.bks"),("123456").toCharArray());
                TrustManagerFactory t = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                t.init(k);
                tmf = t;
                SSLContext s = SSLContext.getInstance("TLSv1.2");
                s.init(null,t.getTrustManagers(),null);
                sslcon = s;

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    public static int getRamAmount() { //https://coderanch.com/t/327792/java/RAM-size-Java
        long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();
        return Math.round((float) memorySize / (1024 * 1024));
    }
    public static boolean isOnline() {
        try {
            URLConnection a = new URL("http://httpbin.org/get").openConnection();
            a.getContent().toString();
            return true;
        } catch (Exception ex) {
            System.out.println(ex);
            return false;
        }
    }
    public static String readFromClasspath(String path) {
        Scanner a = new Scanner(Helper.class.getResourceAsStream(path));
        String newStr = "";
        while (a.hasNext()) {
            newStr += a.next();
        }
        return newStr;
    }
    public static String[] jsonArrayToStringArray(JSONArray arr1) {
        String[] arr = new String[arr1.length()];
        for (int i=0; i < arr.length; i++) {
            arr[i] = arr1.getString(i);
        }
        return arr;
    }
    public static String pathOfJar() {
        try {
            return new File(Helper.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public static JSONObject urls = new JSONObject(Helper.readFromClasspath("/downloadUrls.json"));
    public static LinkedHashMap<String,String> getVersions() {
        LinkedHashMap<String,String> result = new LinkedHashMap<String, String>();
        String raw = Helper.readFromClasspath("/clients.cfg");
        String[] split = raw.replace("\r","").replace("\n","").split(";");
        for (int i=0; i < split.length; i++) {
            String[] tmp = split[i].split(":",2);
            result.put(tmp[0],tmp[1]);
        }
        return result;
    }
    public static void removeMetaInf(String absPath) {
        File tmp = new File(absPath,"META-INF");
        if (tmp.exists()) {
            tmp.delete();
        }
    }
}
