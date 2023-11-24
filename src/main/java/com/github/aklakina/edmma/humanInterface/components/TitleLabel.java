package com.github.aklakina.edmma.humanInterface.components;

import com.github.aklakina.edmma.humanInterface.Resources;

import javax.swing.*;
import java.awt.*;

public class TitleLabel extends JLabel {

    public TitleLabel() {
        super();
        setFont(Resources.getFancyFont().deriveFont(12f));
        //set color to orange
        setForeground(new Color(255, 165, 0));
    }

    public TitleLabel(String text) {
        super(text);
        setFont(Resources.getFancyFont().deriveFont(12f));
        //set color to orange
        setForeground(new Color(255, 165, 0));
    }

}
