package org.featherwhisker.launcher.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import static org.featherwhisker.launcher.util.Helper.isOSX;

public class CurlHttpClient extends HttpClient{
    private String curlCommand = "curl";
    public CurlHttpClient() {}
    public CurlHttpClient(String cmd) {
        curlCommand = cmd;
    }

    private Runtime rt = Runtime.getRuntime();
    public String get(String str, String authHeader) {
        try {
            String[] cmd = {curlCommand,"-H","Authorization: Bearer "+authHeader,str};
            Process proc = rt.exec(cmd);
            BufferedReader out = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(
                    proc.getErrorStream()));
            String s = "";
            String s1;
            while ((s1 = out.readLine()) != null) {
                s += s1 + "\n";
            }
            while ((s1 = err.readLine()) != null) {
                System.out.println(s1);
            }
            return s;
        } catch(Exception ignored) {
            throw new RuntimeException(ignored);
        }
    }

    public void download(String url, String dest) {
        try {
            dest = dest.replace("\\","/").replace("\"","\\\"");
            File tmp = new File(dest.replaceAll("[^\\\\/]+[\\\\/]?$",""));
            try {
                tmp.mkdirs();
            }catch (Exception ignored){}
            String[] cmd = {curlCommand,"-o",dest,url};
            Process proc = rt.exec(cmd);
            BufferedReader out = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(
                    proc.getErrorStream()));
            String s = "";
            String s1;
            while ((s1 = out.readLine()) != null) {
                s += s1 + "\n";
            }
            while ((s1 = err.readLine()) != null) {
                System.out.println(s1);
            }
        } catch(Exception ignored) {
            throw new RuntimeException(ignored);
        }
    }

    public String post(String url1, String json1) {
        try {
            String[] cmd = {
                    curlCommand, "-L",
                    "-d",json1,
                    "-X","POST",
                    "-H","Content-Type: application/json",
                    "-H","Accept: application/json",
                    "-H","x-xbl-contract-version: 1",
                    url1
            };
            Process proc = rt.exec(cmd);
            BufferedReader out = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(
                    proc.getErrorStream()));
            String s = "";
            String s1;
            while ((s1 = out.readLine()) != null) {
                s += s1 + "\n";
            }
            while ((s1 = err.readLine()) != null) {
                System.out.println(s1);
            }
            return s;
        } catch(Exception ignored) {
            throw new RuntimeException(ignored);
        }
    }
}
