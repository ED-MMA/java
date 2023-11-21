package com.github.aklakina.edmma.humanInterface;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class Resources {

    private static final ClassLoader loader = Thread.currentThread().getContextClassLoader();

    private static Font defaultFont = null;
    private static Font FancyFont = null;
    private static Icon wingedIcon = null;
    private static Icon mainIcon = null;

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

    public static void setDefaultFont(Font defaultFont) {
        Resources.defaultFont = defaultFont;
    }

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

    public static void setFancyFont(Font fancyFont) {
        FancyFont = fancyFont;
    }

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

    public static void setWingedIcon(Icon wingedIcon) {
        Resources.wingedIcon = wingedIcon;
    }

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

    public static void setMainIcon(Icon mainIcon) {
        Resources.mainIcon = mainIcon;
    }
}
