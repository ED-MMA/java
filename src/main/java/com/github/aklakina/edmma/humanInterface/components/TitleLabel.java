package com.github.aklakina.edmma.humanInterface.components;

import com.github.aklakina.edmma.humanInterface.Resources;

import javax.swing.*;
import java.awt.*;

/**
 * This class extends JLabel to provide a title label with specific characteristics.
 * It sets the font to the fancy font derived at 12f and the foreground color to orange.
 */
public class TitleLabel extends JLabel {

    /**
     * The default constructor sets the font and foreground color of the label.
     */
    public TitleLabel() {
        super();
        setFont(Resources.getFancyFont().deriveFont(12f));
        //set color to orange
        setForeground(new Color(255, 165, 0));
    }

    /**
     * This constructor sets the text, font and foreground color of the label.
     *
     * @param text The text to be displayed on the label.
     */
    public TitleLabel(String text) {
        super(text);
        setFont(Resources.getFancyFont().deriveFont(12f));
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