package org.featherwhisker.launcher.util;

import org.featherwhisker.launcher.Main;
import org.featherwhisker.launcher.http.HttpClient;
import org.json.JSONObject;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ArrayList;

import static org.featherwhisker.launcher.Main.setStatus;
import static org.featherwhisker.launcher.util.Helper.*;

public class MinecraftLauncher {
    // misc URLs
    public static final String librariesBase = "https://raw.githubusercontent.com/j5mclaunch/lwjgl/refs/heads/main/";

    //  client stuff
    private final Map<String,String> clientUrls = new HashMap<String, String>();
    private String[] libraries = {
            "jinput.jar",
            "lwjgl.jar",
            "lwjgl_util.jar"
    };
    private final Map<String,String> natives = new HashMap<String, String>();

    private static HttpClient http = Helper.getHttpClient();

    private String sessionId = "";
    public String plrUuid = "";
    public String userName = "";
    public MinecraftLauncher() {
        clientUrls.put("1.5.2","https://launcher.mojang.com/v1/objects/465378c9dc2f779ae1d6e8046ebc46fb53a57968/client.jar");
        clientUrls.put("1.4.7","https://launcher.mojang.com/v1/objects/53ed4b9d5c358ecfff2d8b846b4427b888287028/client.jar");
        clientUrls.put("1.3.2","https://launcher.mojang.com/v1/objects/c2efd57c7001ddf505ca534e54abf3d006e48309/client.jar");
        clientUrls.put("1.2.5","https://launcher.mojang.com/v1/objects/4a2fac7504182a97dcbcd7560c6392d7c8139928/client.jar");
        clientUrls.put("1.1","https://launcher.mojang.com/v1/objects/f690d4136b0026d452163538495b9b0e8513d718/client.jar");
        clientUrls.put("1.0.0","https://launcher.mojang.com/v1/objects/b679fea27f2284836202e9365e13a82552092e5d/client.jar");
        clientUrls.put("b1.8.1","https://launcher.mojang.com/v1/objects/6b562463ccc2c7ff12ff350a2b04a67b3adcd37b/client.jar");
        clientUrls.put("b1.7.3","https://launcher.mojang.com/v1/objects/43db9b498cb67058d2e12d394e6507722e71bb45/client.jar");

        natives.put("windows","natives/windows_natives.zip");
        natives.put("linux","natives/linux_natives.zip");
        natives.put("macosx","natives/macosx_natives.zip");
        natives.put("solaris","natives/solaris_natives.zip");
    }
    public void downloadAssets() {
        String assetBase = getMinecraftFolder()+"/resources/";
        File a = new File(assetBase);
        if (!a.exists()) {
            printf("Downloading resources");
            a.mkdirs();
            String tmp = http.get("https://launchermeta.mojang.com/v1/packages/3d8e55480977e32acd9844e545177e69a52f594b/pre-1.6.json","");
            JSONObject assets = new JSONObject(tmp).getJSONObject("objects");
            Iterator<String> keys = assets.keys();
            while (keys.hasNext()) {
                //https://resources.download.minecraft.net/
                String key = keys.next();
                printf("Downloading "+key);
                String hash = assets.getJSONObject(key).getString("hash");
                String url = "https://resources.download.minecraft.net/"+hash.substring(0,2)+"/"+hash;
                http.download(url,assetBase+key);
            }
        }

    }
    public void refreshAuth() {
        String mcHome = getMinecraftFolder();
        File info = new File(mcHome+"/j5mclaunch.json");
        if (info.exists()) {
            MinecraftAuth.loadTokens(mcHome+"/j5mclaunch.json");
            if (MinecraftAuth.getUsername()) {
                userName = MinecraftAuth.username;
                plrUuid = MinecraftAuth.uuid;
                sessionId = MinecraftAuth.minecraft_token;
                setStatus("Welcome "+userName);
                Main.login.setVisible(false);
            } else if (MinecraftAuth.getAccessToken()){
                MinecraftAuth.getXboxToken();
                MinecraftAuth.getMinecraftToken();
                MinecraftAuth.getUsername();
                userName = MinecraftAuth.username;
                plrUuid = MinecraftAuth.uuid;
                sessionId = MinecraftAuth.minecraft_token;
                if (!plrUuid.equals("") && !userName.equals("") && !sessionId.equals("")) {
                    setStatus("Welcome "+userName);
                    MinecraftAuth.saveTokens(mcHome+"/j5mclaunch.json");
                    Main.login.setVisible(false);
                } else {
                    userName = "";
                    plrUuid = "";
                    sessionId = "";
                }
            }
        }
    }
    public void login() {
        final JFrame f = new JFrame();
        f.setLayout(null);
        f.setLocationRelativeTo(null);
        f.setSize(650,75);
        f.getContentPane().setPreferredSize(new Dimension(650, 100));
        f.pack();
        f.setResizable(false);
        JLabel l = new JLabel("Go to the following URL and sign in. Copy the URL of the blank page afterwards and paste it into the other box");
        l.requestFocus();
        l.setBounds(5,0,645,25);
        f.add(l);
        JTextField t = new JTextField(MinecraftAuth.manualUrl);
        t.setEditable(false);
        t.setBounds(0,25,650,25);
        f.add(t);
        final JTextField t1 = new JTextField();
        t1.setBounds(0,50,650,25);
        f.add(t1);
        JButton done = new JButton("Done");
        done.setBounds((650/2) - 100,75,100,25);
        done.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        String path = t1.getText();
                        f.setVisible(false);
                        f.dispose();
                        MinecraftAuth.getMicrosoftTokens(path);
                        MinecraftAuth.getXboxToken();
                        MinecraftAuth.getMinecraftToken();
                        if (MinecraftAuth.getUsername()) {
                            userName = MinecraftAuth.username;
                            plrUuid = MinecraftAuth.uuid;
                            sessionId = MinecraftAuth.minecraft_token;
                            setStatus("Welcome "+userName);
                            MinecraftAuth.saveTokens(getMinecraftFolder()+"/j5mclaunch.json");
                            Main.login.setVisible(false);
                        }
                    }
                });
        f.add(done);
        f.setVisible(true);

    }
    public String[] getClientVersions() {
        return new String[]{"1.5.2", "1.4.7","1.3.2","1.2.5","1.1","1.0.0","b1.8.1","b1.7.3"};
    }
    public String getMinecraftFolder() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            return System.getProperty("user.home")+"/Library/Application Support/minecraft";
        } else if (OS.contains("win")) {
            return System.getenv("APPDATA")+"/.minecraft";
        } else {
            return System.getProperty("user.home")+"/.minecraft";
        }
    }
    public void downloadVersion(String s) {
        String mcHome = getMinecraftFolder();
        File a = new File(mcHome+"/versions/"+s+".jar");
        if (!a.exists()) {
            printf("Downloading version " + s);
            //popup("Downloading version " + s);
            http.download(clientUrls.get(s),mcHome+"/versions/"+s+".jar");
        } else {
            printf("Version "+s+" already downloaded, skipping");
        }
    }
    public void downloadLibraries() {
        String mcHome = getMinecraftFolder();
        printf("Downloading libraries");
        for (String s: libraries) {
            File a = new File(mcHome+"/bin/"+s);
            if (!a.exists()) {
                printf("Downloading library " + s);
                http.download(librariesBase + s, mcHome + "/bin/" + s);
            } else {
                printf(s+" already downloaded, skipping");
            }
        }
        File n = new File(mcHome+"/bin/natives");
        if (!n.exists()) {
            printf("Natives not found!");
            try{
                String sys = "";
                String tmp = System.getProperty("java.io.tmpdir");
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                    sys = "macosx";
                } else if (OS.contains("win")) {
                    sys = "windows";
                } else if (OS.contains("nix")) {
                    sys = "linux";
                } else {
                    sys = "solaris";
                }
                printf("Downloading natives");
                http.download(librariesBase+natives.get(sys),tmp+"/mc_natives.zip");
                printf("Extracting natives");
                n.mkdirs();
                Helper.unzip(tmp+"/mc_natives.zip",n.getAbsolutePath());
            }catch(Exception ex){
                System.out.println(ex);
                error(ex.getMessage());
            }
        }
        printf("Done!");
    }
    public void setupMinecraftFolder() {
        String mcHome = getMinecraftFolder();
        Helper.makeDir(mcHome);
        Helper.makeDir(mcHome+"/bin");
        Helper.makeDir(mcHome+"/versions");
        Helper.makeDir(mcHome+"/texturepacks");
    }

    public void launchGame(String v) {
        if (plrUuid.equals("") || userName.equals("") || sessionId.equals("")) {
            error("You need to sign in to play!");
            return;
        }
        String mcHome = getMinecraftFolder();
        ArrayList<String> args = new ArrayList<String>();
        args.add("java");
        args.add("-Dsun.java2d.noddraw=true");
        args.add("-Dsun.java2d.d3d=false");
        args.add("-Dsun.java2d.opengl=false");
        args.add("-Dsun.java2d.pmoffscreen=false");
        if (isOSX()) {
            //args.add("-XstartOnFirstThread");
            args.add("-Xdock:name=\"Minecraft "+v+"\"");
            args.add("-Xdock:icon="+getMinecraftFolder()+"/resources/icons/minecraft.icns");
            args.add("-Dcom.apple.awt.CocoaComponent.CompatibilityMode=false");
        }
        args.add("-Xmx256M");
        if (getJavaVer() <= 11) {
            args.add("-XX:+UseConcMarkSweepGC");
            args.add("-XX:+CMSIncrementalMode");
        }
        args.add("-XX:-UseAdaptiveSizePolicy");
        args.add("-Xmn84M");
        args.add("-Djava.library.path="+mcHome+"/bin/natives");
        String classPathStr = mcHome+"/versions/"+v+".jar;"+mcHome+"/bin/lwjgl.jar;"+mcHome+"/bin/lwjgl_util.jar;"+mcHome+"/bin/jinput.jar;";
        args.add("-cp");
        if (isWindows()) args.add(classPathStr);
        else args.add(classPathStr.replace(";",":"));
        args.add("net.minecraft.client.Minecraft");

        args.add(userName);
        args.add(sessionId);
       try {
            Main.frame.setEnabled(false);
            Main.frame.setFocusable(false);
            Main.frame.removeAll();
            Main.frame.dispose();
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
        } catch(Exception ex) {
            ex.printStackTrace();
            error(ex.getMessage());
        }

    }
}
