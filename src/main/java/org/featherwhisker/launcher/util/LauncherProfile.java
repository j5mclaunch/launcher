package org.featherwhisker.launcher.util;

import org.featherwhisker.launcher.Main;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class LauncherProfile {
    private static final String mcHome = Main.mclaunch.getMinecraftFolder();

    public static boolean betacraftProxy = false;
    public static String selectedVersion = "1.2.5";

    public static void loadProfile() {
        File info = new File(mcHome+"/j5mclaunch-profile.json");
        if (info.exists()) {
            try {
                Scanner a = new Scanner(info);
                String json = "";
                while (a.hasNextLine()) {
                    json += "\n"+a.nextLine();
                }
                JSONObject b = new JSONObject(json);
                if (b.has("proxy") && b.has("ver")) {
                    betacraftProxy = b.getBoolean("proxy");
                    selectedVersion = b.getString("ver");
                }
            } catch(Exception ignored) {
                System.out.println("Failed to load profile settings");
            }
        }
    }
    public static void saveProfile() {
        try {
            FileWriter file = new FileWriter(mcHome+"/j5mclaunch-profile.json");
            JSONObject json = new JSONObject();
            json.put("proxy",betacraftProxy);
            json.put("ver",selectedVersion);
            file.write(json.toString());
            file.close();
        } catch(Exception ignored) {
            System.out.println("Failed to save profile settings");
        }
    }
    public static void setVersion(String str) {
        selectedVersion = str;
    }

    public static void setProxyEnabled(boolean str) {
        betacraftProxy = str;
    }
}
