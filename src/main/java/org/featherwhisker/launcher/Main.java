package org.featherwhisker.launcher;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;

import org.featherwhisker.launcher.util.Helper;
import org.featherwhisker.launcher.util.MinecraftLauncher;

public class Main {

    public static JFrame frame;
    static JButton launch;
    static JLabel status;
    public static JButton login;
    static JComboBox<String> vs;

    private static MinecraftLauncher mclaunch;
    private static String ver = "1.2.5";
    public static void main(String[] args) {
        if (Helper.isOSX()) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            System.setProperty("apple.awt.application.name", "j5mclaunch");
            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "j5mclaunch");
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
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
        System.out.println("-------------------------------\n");

        mclaunch = new MinecraftLauncher();

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
        status.setBounds(5,5,180,25);
        status.setVisible(true);
        frame.add(status);

        mclaunch.setupMinecraftFolder();

        launch = new JButton("Launch");
        launch.setBounds(195,45,100,25);
        launch.setVisible(true);
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
        login.setBounds(195,15,100,25);
        login.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (mclaunch.userName == "" || mclaunch.plrUuid == "") {
                            mclaunch.login();
                        }
                    }
                });
        login.setVisible(true);
        frame.add(login);

        vs = new JComboBox<String>(mclaunch.getClientVersions());
        vs.setBounds(5,45,100,25);
        vs.setVisible(true);
        vs.setSelectedIndex(3);
        vs.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        ver = vs.getSelectedItem().toString();
                    }
                });
        frame.add(vs);
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
}