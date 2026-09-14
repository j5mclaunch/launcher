package org.j5mclaunch.launcher;

import java.io.*;
import java.util.ArrayList;
import java.util.Locale;

public class Main {
    public static String pathOfJar() {
        try {
            return new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getPath();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    public static String getMinecraftFolder() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            return System.getProperty("user.home")+"/Library/Application Support/minecraft";
        } else if (OS.contains("win")) {
            return System.getenv("APPDATA")+"/.minecraft";
        } else {
            return System.getProperty("user.home")+"/.minecraft";
        }
    }
    public static String LAUNCHER_LIBS = getMinecraftFolder()+"/bouncycastle-1.86";
    public static void copyFileFromPath(String from, String dest) throws IOException {
        File dest1 = new File(dest+"/"+from);
        InputStream str = Main.class.getResourceAsStream("/libs/"+from);
        FileOutputStream f = new FileOutputStream(dest1);
        int s = str.read();
        while (s != -1) {
            f.write(s);
            s = str.read();
        }
        f.flush();
        f.close();
    }
    public static void main(String[] a) throws IOException {
        File folder = new File(LAUNCHER_LIBS);
        if (!folder.exists()) {
            folder.mkdirs();
            System.out.println("First launch may take a few minutes");
            copyFileFromPath("bcprov-jdk15to18-1.86.jar",LAUNCHER_LIBS);
            copyFileFromPath("bctls-jdk15to18-1.86.jar",LAUNCHER_LIBS);
            copyFileFromPath("bcutil-jdk15to18-1.86.jar",LAUNCHER_LIBS);
        }
        ArrayList<String> args = new ArrayList<String>();
        args.add(System.getProperty("java.home")+"/bin/java");
        args.add("-cp");
        String classPathStr = pathOfJar()+";"+LAUNCHER_LIBS+"/bcprov-jdk15to18-1.86.jar;"+LAUNCHER_LIBS+"/bctls-jdk15to18-1.86.jar;"+LAUNCHER_LIBS+"/bcutil-jdk15to18-1.86.jar;";
        if (System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH).contains("win")) args.add(classPathStr.replace("/","\\"));
        else args.add(classPathStr.replace(";",":"));
        args.add("org.j5mclaunch.launcher.Launcher");
        ProcessBuilder proc1 = new ProcessBuilder(args);
        Process proc = proc1.directory(new File(getMinecraftFolder())).redirectErrorStream(true).start();
        BufferedReader out = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
        BufferedReader err = new BufferedReader(new InputStreamReader(
                proc.getErrorStream()));
        String s1;
        while ((s1 = out.readLine()) != null) {
            System.out.println(s1);
        }
        while ((s1 = err.readLine()) != null) {
            System.out.println(s1);
        }
        System.exit(0);
    }
}
