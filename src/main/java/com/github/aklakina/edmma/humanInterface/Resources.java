package com.github.aklakina.edmma.humanInterface;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * This class provides resources such as fonts and icons for the application.
 * It uses a class loader to load the resources from the file system.
 * The resources are stored as static variables and are loaded only once when they are first accessed.
 */
public class Resources {

    // The class loader used to load the resources.
    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    // The default font used in the application.
    private static Font defaultFont = null;
    // The fancy font used in the application.
    private static Font FancyFont = null;
    // The winged icon used in the application.
    private static Icon wingedIcon = null;
    // The main icon used in the application.
    private static Icon mainIcon = null;

    /**
     * This method returns the default font.
     * If the font has not been loaded yet, it loads the font from the file system.
     *
     * @return The default font.
     */
    public static Font getDefaultFont() {
        if (defaultFont == null) {
            try {
                setDefaultFont(Font.createFont(Font.TRUETYPE_FONT, new File(loader.getResource("Fonts/EUROCAPS.ttf").getFile())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultFont;
    }

    /**
     * This method sets the default font.
     *
     * @param defaultFont The font to be set as the default font.
     */
    public static void setDefaultFont(Font defaultFont) {
        Resources.defaultFont = defaultFont;
    }

    /**
     * This method returns the fancy font.
     * If the font has not been loaded yet, it loads the font from the file system.
     *
     * @return The fancy font.
     */
    public static Font getFancyFont() {
        if (FancyFont == null) {
            try {
                setFancyFont(Font.createFont(Font.TRUETYPE_FONT, new File(loader.getResource("Fonts/Gugi-Regular.ttf").getFile())));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return FancyFont;
    }

    /**
     * This method sets the fancy font.
     *
     * @param fancyFont The font to be set as the fancy font.
     */
    public static void setFancyFont(Font fancyFont) {
        FancyFont = fancyFont;
    }

    /**
     * This method returns the winged icon.
     * If the icon has not been loaded yet, it loads the icon from the file system.
     *
     * @return The winged icon.
     */
    public static Icon getWingedIcon() {
        if (wingedIcon == null) {
            try {
                setWingedIcon(new ImageIcon(loader.getResource("Logos/Wing.svg")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return wingedIcon;
    }

    /**
     * This method sets the winged icon.
     *
     * @param wingedIcon The icon to be set as the winged icon.
     */
    public static void setWingedIcon(Icon wingedIcon) {
        Resources.wingedIcon = wingedIcon;
    }

    /**
     * This method returns the main icon.
     * If the icon has not been loaded yet, it loads the icon from the file system.
     *
     * @return The main icon.
     */
    public static Icon getMainIcon() {
        if (mainIcon == null) {
            try {
                setMainIcon(new ImageIcon(loader.getResource("Logos/elite-dangerous-vektor.svg")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mainIcon;
    }

    /**
     * This method sets the main icon.
     *
     * @param mainIcon The icon to be set as the main icon.
     */
    public static void setMainIcon(Icon mainIcon) {
        Resources.mainIcon = mainIcon;
    }
}