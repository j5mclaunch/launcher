package org.featherwhisker.launcher.http;

public abstract class HttpClient {
    public abstract String get(String str,String authHeader);
    public abstract void download(String url, String dest);
    public abstract String post(String url1, String json1);
}
