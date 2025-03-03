package org.j5mclaunch.shim;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;
import org.j5mclaunch.launcher.util.Helper;

public class Launch {
    public static void main(String[] args) {
        Runtime run = Runtime.getRuntime();
        String ver = System.getProperty("org.j5mclaunch.mcver");
        String uuid = System.getProperty("org.j5mclaunch.uuid");
        if (ver == null) {
            ver = "1.2.5";
        }
        if (uuid == null) {
            uuid = "OFFLINE MODE";
        }
        System.out.println("-------Running Pre-Launch------");
        System.out.println("java.version: "+System.getProperty("java.version"));
        System.out.println("os.arch: "+System.getProperty("os.arch"));
        System.out.println("cpu cores: "+run.availableProcessors());
        System.out.println("allocated ram: "+run.maxMemory()/1024/1024+" Mb");
        System.out.println("launched version: "+System.getProperty("org.j5mclaunch.mcver"));
        System.out.println("betacraft proxy: "+(System.getProperty("http.proxyHost") != null && System.getProperty("http.proxyHost").equals(Helper.urls.getString("betacraft"))));
        System.out.println("-------------------------------\n");
        try {
            try {
                Class.forName( "net.minecraft.client.Minecraft" );
                Minecraft.main(args);
            }
            catch(ClassNotFoundException ex) {
                Main.main(new String[]{"--username",args[0],"--session",args[1],"--accessToken",args[1],"--userType","msa","--version",ver,"--uuid",uuid});
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
