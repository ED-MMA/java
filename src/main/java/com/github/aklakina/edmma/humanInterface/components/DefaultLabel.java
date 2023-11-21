package com.github.aklakina.edmma.humanInterface.components;

import com.github.aklakina.edmma.humanInterface.Resources;

import javax.swing.*;
import java.awt.*;

public class DefaultLabel extends JLabel {

    public DefaultLabel() {
        super();
        setFont(Resources.getDefaultFont().deriveFont(10f));
        //set color to orange
        setForeground(new Color(255, 165, 0));
    }

}
