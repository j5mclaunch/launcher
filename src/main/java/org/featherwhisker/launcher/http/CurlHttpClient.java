package org.featherwhisker.launcher.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CurlHttpClient extends HttpClient{
    private Runtime rt = Runtime.getRuntime();
    public String get(String str, String authHeader) {
        try {
            Process proc = rt.exec("curl --header \"Authorization: Bearer "+authHeader+"\" "+str);
            BufferedReader out = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(
                    proc.getErrorStream()));
            String s = "";
            String s1;
            while ((s1 = out.readLine()) != null) {
                s += s1 + "\n";
            }
            while ((s1 = out.readLine()) != null) {
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
            Process proc = rt.exec("curl -o \""+dest+"\" "+url);
            BufferedReader out = new BufferedReader(new
                    InputStreamReader(proc.getInputStream()));
            BufferedReader err = new BufferedReader(new InputStreamReader(
                    proc.getErrorStream()));
            String s = "";
            String s1;
            while ((s1 = out.readLine()) != null) {
                s += s1 + "\n";
            }
            while ((s1 = out.readLine()) != null) {
                System.out.println(s1);
            }
        } catch(Exception ignored) {
            throw new RuntimeException(ignored);
        }
    }

    public String post(String url1, String json1) {
        try {
            Process proc = rt.exec("curl -d \""+json1.replace("\"","\\\"")+"\" -X POST -H \"Content-Type: application/json\" -H \"Accept: application/json\" "+url1);
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
