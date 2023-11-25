package com.github.aklakina.edmma.humanInterface.components;

import com.github.aklakina.edmma.humanInterface.Resources;

import javax.swing.*;
import java.awt.*;

/**
 * This class extends JLabel to provide a default label with specific characteristics.
 * It sets the font to the default font derived at 10f and the foreground color to orange.
 */
public class DefaultLabel extends JLabel {

    /**
     * The constructor sets the font and foreground color of the label.
     */
    public DefaultLabel() {
        super();
        setFont(Resources.getDefaultFont().deriveFont(10f));
        //set color to orange
        setForeground(new Color(255, 165, 0));
    }

    /**
     * This method sets the font and foreground color of the label to that of the given label.
     *
     * @param label The label whose font and foreground color are to be copied.
     */
    public void setInnerLabel(JLabel label) {
        setFont(label.getFont());
        setForeground(label.getForeground());
    }

}