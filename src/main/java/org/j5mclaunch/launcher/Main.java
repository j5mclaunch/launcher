package org.j5mclaunch.launcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.plaf.FontUIResource;

import org.j5mclaunch.launcher.util.Helper;
import org.j5mclaunch.launcher.util.LauncherProfile;
import org.j5mclaunch.launcher.util.MinecraftLauncher;
import org.json.JSONObject;

public class Main {

    public static JFrame frame;
    public static JButton launch;
    static JLabel status;
    public static JButton login;
    static JCheckBox proxy;
    static JComboBox<String> vs;
    public static MinecraftLauncher mclaunch;
    private static String ver = "1.2.5";
    public static void main(String[] args) {
        if (Helper.isOSX()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "j5mclaunch");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "j5mclaunch");
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            java.util.Enumeration keys = UIManager.getDefaults().keys();
            FontUIResource f = new javax.swing.plaf.FontUIResource("Sansserif", Font.TRUETYPE_FONT,13);
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get (key);
                if (value instanceof javax.swing.plaf.FontUIResource)
                    UIManager.put (key, f);
            }
        } catch(Exception e) {
            System.out.println("Failed to set look and feel! :(");
        }
        System.out.println("-------System information------");
        System.out.println("java.vendor: "+System.getProperty("java.vendor"));
        System.out.println("java.version: "+System.getProperty("java.version"));
        System.out.println("java TLSv1.3: "+Helper.javaClientSupported());
        System.out.println("os.name: "+System.getProperty("os.name"));
        System.out.println("os.version: "+System.getProperty("os.version"));
        System.out.println("os.arch: "+System.getProperty("os.arch"));
        System.out.println("system ram: "+Helper.getRamAmount()+" Mb");
        System.out.println("-------------------------------\n");

        mclaunch = new MinecraftLauncher();
        LauncherProfile.loadProfile();
        frame = new JFrame("j5mclaunch");

        frame.setSize(300,75);
        frame.setName("j5mclaunch");
        frame.setTitle("Minecraft Launcher");
        try {
            frame.setIconImage(ImageIO.read(Main.class.getResource("/icon.png")));
        }catch(Exception ex) {
            System.out.println("Failed to set window icon! :(");
            System.out.println(ex);
        }
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setPreferredSize(new Dimension(300, 75));
        frame.pack();

        status = new JLabel("Please log in to play.");
        status.setBounds(10,5,180,25);
        status.setVisible(true);
        frame.add(status);

        mclaunch.setupMinecraftFolder();

        launch = new JButton("Launch");
        launch.setBounds(195,45,100,25);
        launch.setVisible(false);
        launch.setEnabled(false);
        launch.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        mclaunch.downloadVersion(ver);
                        mclaunch.downloadLibraries();
                        mclaunch.downloadAssets();
                        mclaunch.launchGame(ver);
                    }
                });
        frame.add(launch);

        login = new JButton("Login");
        login.setBounds(195,45,100,25);
        login.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (mclaunch.userName == "" || mclaunch.plrUuid == "") {
                            mclaunch.login();
                        }
                    }
                });
        login.setVisible(false);
        login.setEnabled(false);
        frame.add(login);

        vs = new JComboBox<String>(mclaunch.getClientVersions());
        //vs.setBounds(5,45,100,25);
        vs.setBounds(195,15,100,25);
        vs.setVisible(true);
        vs.setSelectedIndex(3);
        vs.setSelectedIndex(Arrays.asList(mclaunch.getClientVersions()).indexOf(LauncherProfile.selectedVersion));
        ver = LauncherProfile.selectedVersion;
        vs.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ver = vs.getSelectedItem().toString();
                        LauncherProfile.setVersion(ver);
                    }
                });
        frame.add(vs);

        proxy = new JCheckBox("Betacraft Proxy");
        proxy.createToolTip().setTipText("Fixes skins, needed for online mode on b1.7.3");
        proxy.setSelected(LauncherProfile.betacraftProxy);
        proxy.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        LauncherProfile.setProxyEnabled(proxy.isSelected());
                    }
                });
        proxy.setBounds(5,45,125,25);
        proxy.setVisible(true);
        frame.add(proxy);

        frame.setVisible(true);
        mclaunch.refreshAuth();
    }
    public static void setStatus(String txt) {
        status.setText(txt);
        status.repaint();
        status.revalidate();
        status.paintImmediately(status.getVisibleRect());
        frame.repaint();
    }
    public static void setPlayEnabled() {
        login.setVisible(false);
        login.setEnabled(false);
        launch.setVisible(true);
        launch.setEnabled(true);
        launch.revalidate();
        launch.repaint();
        launch.paintImmediately(launch.getVisibleRect());
        frame.repaint();
    }
    public static void setPlayDisabled() {
        login.setVisible(true);
        login.setEnabled(true);
        launch.setVisible(false);
        launch.setEnabled(false);
        launch.revalidate();
        launch.repaint();
        launch.paintImmediately(launch.getVisibleRect());
        frame.repaint();
    }
}