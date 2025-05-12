package org.j5mclaunch.launcher.util;

import com.centerkey.utils.BareBonesBrowserLaunch;
import org.j5mclaunch.launcher.Main;
import org.j5mclaunch.launcher.http.HttpClient;
import org.json.JSONObject;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;

import static org.j5mclaunch.launcher.Main.*;
import static org.j5mclaunch.launcher.util.Helper.*;

public class MinecraftLauncher {
    public JSONObject urls = Helper.urls;
    // misc URLs
    public final String librariesBase = urls.getString("lwjglUrl");

    //  client stuff
    private final Map<String,String> clientUrls = Helper.getVersions();
    private String[] libraries = Helper.jsonArrayToStringArray(urls.getJSONArray("lwjgl"));

    private final Map<String,String> natives = new HashMap<String, String>();

    private static HttpClient http = Helper.getHttpClient();

    private String sessionId = "";
    public String plrUuid = "";
    public String userName = "";
    public MinecraftLauncher() {
        for (Iterator<String> it = urls.getJSONObject("natives").keys(); it.hasNext(); ) {
            String key = it.next();
            natives.put(key,urls.getJSONObject("natives").getString(key));
        }
    }
    public void downloadAssets() {
        String assetBase = getMinecraftFolder()+"/resources/";
        File a = new File(assetBase);
        if (!a.exists()) {
            printf("Downloading resources");
            a.mkdirs();
            String tmp = http.get(urls.getString("assetUrl"),"");
            String tmp1 = urls.getString("assetBase");
            JSONObject assets = new JSONObject(tmp).getJSONObject("objects");
            Iterator<String> keys = assets.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                printf("Downloading "+key);
                String hash = assets.getJSONObject(key).getString("hash");
                String url = tmp1+hash.substring(0,2)+"/"+hash;
                http.download(url,assetBase+key);
            }
        }

    }
    public void refreshAuth() {
        String mcHome = getMinecraftFolder();
        File info = new File(mcHome+"/j5mclaunch.json");
        boolean online = Helper.isOnline();
        if (info.exists()) {
            setStatus("Logging in...");
            MinecraftAuth.loadTokens(mcHome+"/j5mclaunch.json");
            if (!online && MinecraftAuth.username.length() != 0) {
                userName = MinecraftAuth.username;
                plrUuid = "NO INTERNET";
                sessionId = "NO INTERNET";
                setStatus("Welcome "+userName+" *OFFLINE*");
                setPlayEnabled();
            } else if (!online) {
                error("You need to be online to sign in and download the game!");
                System.exit(1);
            } else {
                try {
                    if (MinecraftAuth.getUsername()) {
                        userName = MinecraftAuth.username;
                        plrUuid = MinecraftAuth.uuid;
                        sessionId = MinecraftAuth.minecraft_token;
                        setStatus("Welcome "+userName);
                        Main.setPlayEnabled();
                    } else if (MinecraftAuth.getAccessToken()){
                        MinecraftAuth.getXboxToken();
                        MinecraftAuth.getMinecraftToken();
                        MinecraftAuth.getUsername();
                        userName = MinecraftAuth.username;
                        plrUuid = MinecraftAuth.uuid;
                        sessionId = MinecraftAuth.minecraft_token;
                        if (plrUuid.length() != 0 && userName.length() != 0 && sessionId.length() != 0) {
                            setStatus("Welcome "+userName);
                            MinecraftAuth.saveTokens(mcHome+"/j5mclaunch.json");
                            setPlayEnabled();
                        } else {
                            setStatus("Please log in to play.");
                            userName = "";
                            plrUuid = "";
                            sessionId = "";
                            setPlayDisabled();
                        }
                    } else {
                        setPlayDisabled();
                    }
                } catch(Exception ex) {
                    setStatus("Login expired.");
                    setPlayDisabled();
                }
            }
        } else {
            setPlayDisabled();
            if (!online) {
                error("You need to be online to sign in and download the game!");
                System.exit(1);
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
        done.setBounds((650/2),75,100,25);
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
                            Main.setPlayEnabled();
                        } else {
                            login.setVisible(true);
                            login.setEnabled(true);
                        }
                    }
                });
        f.add(done);
        JButton copy = new JButton("Open URL");
        copy.setBounds((650/2) - 100,75,100,25);
        copy.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        BareBonesBrowserLaunch.openURL(MinecraftAuth.manualUrl);
                    }
                });
        f.add(copy);
        f.setVisible(true);

    }
    private String[] versions;
    public String[] getClientVersions() {
        if (versions == null) {
            versions = new String[clientUrls.size()];
            for (int i=0; i < versions.length; i++) {
                versions[i] = (String) clientUrls.keySet().toArray()[i];
            }
        }
        return versions;
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
    public void downloadVersion(String s) {
        String mcHome = getMinecraftFolder();
        File a = new File(mcHome+"/versions/"+s+".jar");
        if (!a.exists()) {
            printf("Downloading version " + s);
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
        if (Helper.getJavaVer() > 5) {
            for (Iterator<String> it = urls.getJSONObject("j6libraries").keys(); it.hasNext(); ) {
                String key = it.next();
                String url = urls.getJSONObject("j6libraries").getString(key);
                File a = new File(mcHome+"/libraries/"+key);
                if (!a.exists()) {
                    printf("Downloading library " + key);
                    http.download(url, mcHome + "/libraries/" + key);
                } else {
                    printf(key+" already downloaded, skipping");
                }
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
        LauncherProfile.saveProfile();
        String mcHome = getMinecraftFolder();
        ArrayList<String> args = new ArrayList<String>();
        File tmp = new File("/System/Library/Frameworks/JavaVM.framework/Versions/current/Commands/java");
        if (tmp.exists()) {
            args.add(tmp.getAbsolutePath());
        } else {
            args.add("java");
        }
        args.add("-Dsun.java2d.noddraw=true");
        args.add("-Dsun.java2d.d3d=false");
        args.add("-Dsun.java2d.opengl=false");
        args.add("-Dsun.java2d.pmoffscreen=false");
        if (isOSX()) {
            args.add("-Xdock:name=Minecraft "+v);
            args.add("-Xdock:icon="+getMinecraftFolder()+"/resources/icons/minecraft.icns");
            args.add("-Dcom.apple.awt.CocoaComponent.CompatibilityMode=false");
        }
        if (LauncherProfile.betacraftProxy) {
            args.add("-Dhttp.proxyHost="+urls.getString("betacraft"));
            args.add("-Djava.util.Arrays.useLegacyMergeSort=true");
        }
        int ramToUse = (Helper.getRamAmount()/2);
        ramToUse = ramToUse - (ramToUse%128) + 128;
        if (ramToUse > 4096) {
            ramToUse = 4096;
        } else if (ramToUse < 256) {
            ramToUse = 256;
        }
        args.add("-Xmx"+ramToUse+"M");
        if (getJavaVer() <= 11) {
            args.add("-XX:+UseConcMarkSweepGC");
            args.add("-XX:+UseTLAB");
            args.add("-XX:+CMSIncrementalMode");
        }
        args.add("-XX:-UseAdaptiveSizePolicy");
        args.add("-Xmn84M");
        args.add("-Djava.library.path="+mcHome+"/bin/natives");
        String classPathStr = pathOfJar()+";"+mcHome+"/versions/"+v+".jar;"+mcHome+"/bin/lwjgl.jar;"+mcHome+"/bin/lwjgl_util.jar;"+mcHome+"/bin/jinput.jar;";
        if (getJavaVer() > 5) {
            for (Iterator<String> it = urls.getJSONObject("j6libraries").keys(); it.hasNext(); ) {
                String key = it.next();
                classPathStr+=mcHome+"/libraries/"+key+";";
            }
        }
        args.add("-Dorg.j5mclaunch.mcver="+v);
        args.add("-Dorg.j5mclaunch.uuid="+plrUuid);
        args.add("-cp");
        if (isWindows()) args.add(classPathStr);
        else args.add(classPathStr.replace(";",":"));
        //args.add("net.minecraft.client.Minecraft");
        args.add("org.j5mclaunch.shim.Launch");

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
                System.out.println(s1.replace(sessionId,"<SESSION ID>"));
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
