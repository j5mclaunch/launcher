package org.featherwhisker.launcher.http;

import org.featherwhisker.launcher.util.Helper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static org.featherwhisker.launcher.util.Helper.error;

public class JavaHttpClient extends HttpClient {
    public JavaHttpClient() {
        if (!Helper.javaClientSupported()) {
            error("Unable to fallback to Java HTTP client!");
            System.exit(1);
        }
    }

    // Most of this is StackOverflow stuff thrown together
    // https://creativecommons.org/licenses/by-sa/4.0/

    @Override
    public String get(String url1, String auth) {
        //https://stackoverflow.com/a/5868033
        try {
            URL url = new URL(url1);
            URLConnection con = url.openConnection();
            con.setRequestProperty("Authorization","Bearer "+auth);
            InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();  // ** WRONG: should use "con.getContentType()" instead but it returns something like "text/html; charset=UTF-8" so this value must be parsed to extract the actual encoding
            encoding = encoding == null ? "UTF-8" : encoding;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            in.close();
            String body = new String(baos.toByteArray(), encoding);
            return body;
        } catch(Exception ex) {
            System.out.println(ex);
            error(ex.getMessage());
            return "";
        }
    }

    @Override
    public void download(String url, String dest) {
        //https://stackoverflow.com/a/921400
        try {
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File tmp = new File(dest.replaceAll("[^\\\\/]+[\\\\/]?$",""));
            try {
                tmp.mkdirs();
            }catch (Exception ignored){}
            FileOutputStream fos = new FileOutputStream(dest);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        } catch(Exception ex) {
            System.out.println(ex);
            error(ex.getMessage());
        }
    }

    public String post(String url1, String json1) {
        try {
            URL url = new URL(url1);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            try {
                http.setRequestMethod("POST"); // PUT is another valid option
                http.setDoOutput(true);
                byte[] out = json1.getBytes();
                int length = out.length;
                http.setFixedLengthStreamingMode(length);
                http.setRequestProperty("Content-Type", " application/json");
                http.connect();
                OutputStream os = http.getOutputStream();
                os.write(out);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                InputStream in = http.getInputStream();
                byte[] buf = new byte[8192];
                int len = 0;
                while ((len = in.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                in.close();
                String body = new String(baos.toByteArray(), "UTF-8");
                return body;
            } catch(IOException ex) {
                error(ex.getMessage());
                System.out.println(http.getResponseMessage());
                System.out.println(ex.getCause());
                return "{}";
            }
        } catch (Exception ex) {
            System.out.println(ex);
            error(ex.getMessage());
            return "{}";
        }
    }
}
