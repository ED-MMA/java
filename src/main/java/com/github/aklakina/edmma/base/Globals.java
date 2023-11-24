package com.github.aklakina.edmma.base;

import com.github.aklakina.edmma.database.orms.GalacticPosition;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Globals {
    public static String DATABASE_URL = "jdbc:h2:./EDMMA";
    public static final String USER_HOME = java.lang.System.getProperty("user.home");
    public static String ELITE_LOG_HOME = USER_HOME + "/Saved Games/Frontier Developments/Elite Dangerous";
    public static int FILE_READER_CHECK_INTERVAL = 2;
    public static TimeUnit FILE_READER_CHECK_INTERVAL_UNIT = TimeUnit.MINUTES;
    public static final Pattern LOG_FILE_NAME_REGEX = Pattern.compile("Journal.*\\.log");
    public static GalacticPosition GALACTIC_POSITION;
}
