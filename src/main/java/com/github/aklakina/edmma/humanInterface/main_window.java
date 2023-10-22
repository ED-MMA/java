package com.github.aklakina.edmma.humanInterface;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.logicalUnit.EventHandler;
import com.github.aklakina.edmma.logicalUnit.Init;
import com.github.aklakina.edmma.machineInterface.WatchDir;
import org.hibernate.SessionFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class main_window {
    private JPanel panel1;
    private JTable table1;
    private JTree tree1;
    private JTabbedPane tabbedPane1;
    private JSlider slider1;
    private JButton copyCalculatedDataToButton;
    private JButton copyFullDataToButton;
    private JButton copyOnlyCompletedDataButton;

    public main_window() {
        copyFullDataToButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public static void main(String[] args) {
        SingletonFactory.getSingleton(Init.class);
        JFrame frame = new JFrame("EDMMA");
        frame.setContentPane(new main_window().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
