package org.j5mclaunch.launcher.util;
import org.j5mclaunch.launcher.http.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class MinecraftAuth {
    private static HttpClient http = Helper.getHttpClient();

    public static String manualUrl = "https://login.live.com/oauth20_authorize.srf?client_id=00000000402B5328&redirect_uri=https://login.live.com/oauth20_desktop.srf&response_type=code&scope=service::user.auth.xboxlive.com::MBI_SSL";

    private static String microsoft_token = ""; //this one doesn't really matter
    private static String microsoft_refresh_token = "";
    public static void getMicrosoftTokens(String url) {
        String code = url.split("code=")[1].split("&")[0];
        JSONObject json = new JSONObject();
        json.put("client_id","00000000402B5328");
        json.put("scope","service::user.auth.xboxlive.com::MBI_SSL");
        json.put("code",code);
        json.put("redirect_uri","https://login.live.com/oauth20_desktop.srf");
        json.put("grant_type","authorization_code");
        String response = http.get("https://login.live.com/oauth20_token.srf?client_id=00000000402B5328&scope=service::user.auth.xboxlive.com::MBI_SSL&redirect_uri=https://login.live.com/oauth20_desktop.srf&grant_type=authorization_code&code="+code,"");
        JSONObject jsonResponse = new JSONObject(response);

        microsoft_token = jsonResponse.getString("access_token");
        microsoft_refresh_token = jsonResponse.getString("refresh_token");
    }
    public static boolean getAccessToken() {
        JSONObject json = new JSONObject();
        json.put("scope","service::user.auth.xboxlive.com::MBI_SSL");
        json.put("client_id","00000000402B5328");
        json.put("grant_type","refresh_token");
        json.put("refresh_token",microsoft_refresh_token);
        String response = http.get("https://login.live.com/oauth20_token.srf?client_id=00000000402B5328&scope=service::user.auth.xboxlive.com::MBI_SSL&grant_type=refresh_token&refresh_token="+microsoft_refresh_token,"");
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.has("access_token")) {
            microsoft_token = jsonResponse.getString("access_token");
            return true;
        }
        return false;
    }

    private static String xsts_userhash;
    private static String xsts_token;
    public static void getXboxToken() {
        JSONObject json = new JSONObject();
        JSONObject properties = new JSONObject();
        properties.put("AuthMethod","RPS");
        properties.put("SiteName","user.auth.xboxlive.com");
        properties.put("RpsTicket",microsoft_token);
        json.put("Properties",properties);
        json.put("RelyingParty","http://auth.xboxlive.com");
        json.put("TokenType","JWT");
        String response = http.post("https://user.auth.xboxlive.com/user/authenticate",json.toString());
        JSONObject jsonResponse = new JSONObject(response);
        String xbl_token = jsonResponse.getString("Token");

        json = new JSONObject();
        properties = new JSONObject();
        properties.put("SandboxId","RETAIL");
        properties.put("UserTokens",new JSONArray().put(xbl_token));
        json.put("Properties",properties);
        json.put("RelyingParty","rp://api.minecraftservices.com/");
        json.put("TokenType","JWT");
        response = http.post("https://xsts.auth.xboxlive.com/xsts/authorize",json.toString());
        jsonResponse = new JSONObject(response);

        xsts_userhash = jsonResponse.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
        xsts_token = jsonResponse.getString("Token");
    }

    public static String minecraft_token = "";
    public static void getMinecraftToken() {
        JSONObject json = new JSONObject();
        json.put("identityToken","XBL3.0 x="+xsts_userhash+";"+xsts_token);
        String response = http.post("https://api.minecraftservices.com/authentication/login_with_xbox",json.toString());
        JSONObject jsonResponse = new JSONObject(response);
        minecraft_token = jsonResponse.getString("access_token");
    }

    public static String uuid = "";
    public static String username = "";
    public static boolean getUsername() {
        String response = http.get("https://api.minecraftservices.com/minecraft/profile",minecraft_token);
        JSONObject jsonResponse = new JSONObject(response);
        if (jsonResponse.has("name") && jsonResponse.has("id")) {
            uuid = jsonResponse.getString("id");
            username = jsonResponse.getString("name");
            System.out.println("uuid: "+uuid);
            System.out.println("username: "+username);
            return true;
        } else {
            return false;
        }
    }

    public static void saveTokens(String path) {
        try {
            FileWriter file = new FileWriter(path);
            JSONObject json = new JSONObject();
            json.put("minecraft",minecraft_token);
            json.put("refresh",microsoft_refresh_token);
            json.put("username",username);
            file.write(json.toString());
            file.close();
        } catch(Exception ex) {
            System.out.println(ex);
            Helper.error(String.valueOf(ex));
        }
    }

    public static void loadTokens(String path) {
        try {
            File file = new File (path);
            Scanner a = new Scanner(file);
            String json = "";
            while (a.hasNextLine()) {
                json += "\n"+a.nextLine();
            }
            JSONObject b = new JSONObject(json);
            if (b.has("minecraft") && b.has("refresh")) {
                minecraft_token = b.getString("minecraft");
                microsoft_refresh_token = b.getString("refresh");
            }
            if (b.has("username")) {
                username = b.getString("username");
            }
        } catch(Exception ex) {
            System.out.println(ex);
            Helper.error(String.valueOf(ex));
        }
    }
}
