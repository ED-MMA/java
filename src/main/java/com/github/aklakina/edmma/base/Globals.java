package com.github.aklakina.edmma.base;

import com.github.aklakina.edmma.database.orms.GalacticPosition;

import java.util.regex.Pattern;

public class Globals {
    public static final String USER_HOME = java.lang.System.getProperty("user.home");
    public static final String ELITE_LOG_HOME = USER_HOME + "/Saved Games/Frontier Developments/Elite Dangerous";
    public static final Pattern LOG_FILE_NAME_REGEX = Pattern.compile("Journal.*\\.log");

    public static GalacticPosition GALACTIC_POSITION;
}
