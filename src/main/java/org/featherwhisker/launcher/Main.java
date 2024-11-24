package org.featherwhisker.launcher;

import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import org.featherwhisker.launcher.http.CurlHttpClient;
import org.featherwhisker.launcher.http.HttpClient;
import org.featherwhisker.launcher.http.JavaHttpClient;
import org.featherwhisker.launcher.util.Helper;
import org.featherwhisker.launcher.util.MinecraftLauncher;

public class Main {

    static JFrame frame;
    static JButton launch;
    static JLabel status;
    static JButton login;
    static JComboBox<String> vs;

    private static MinecraftLauncher mclaunch = new MinecraftLauncher();
    private static String ver = "1.2.5";
    public static void main(String[] args) {
        frame = new JFrame("j5mclaunch");

        frame.setSize(300,75);
        frame.setName("j5mclaunch");
        frame.setTitle("Minecraft Launcher");
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