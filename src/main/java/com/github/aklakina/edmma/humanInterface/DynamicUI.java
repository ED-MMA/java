package com.github.aklakina.edmma.humanInterface;

import javax.swing.*;
import java.awt.*;

public class DynamicUI {

    private JPanel mainPanel;

    public DynamicUI() {
        JFrame frame = new JFrame("EDMMA");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.pack();
        frame.setVisible(true);

        GridLayout gridLayout = new GridLayout(0, 1);
        this.mainPanel = new JPanel(gridLayout);
        frame.setContentPane(this.mainPanel);

    }

}
