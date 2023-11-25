package com.github.aklakina.edmma.base;

import com.github.aklakina.edmma.database.orms.GalacticPosition;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * This class contains global constants and variables used throughout the application.
 */
public class Globals {
    // The user's home directory
    public static final String USER_HOME = java.lang.System.getProperty("user.home");

    // Regular expression pattern for log file names
    public static final Pattern LOG_FILE_NAME_REGEX = Pattern.compile("Journal.*\\.log");

    // URL for the database connection
    public static String DATABASE_URL = "jdbc:h2:./EDMMA";

    // Path to the Elite Dangerous log files
    public static String ELITE_LOG_HOME = USER_HOME + "/Saved Games/Frontier Developments/Elite Dangerous";

    // Interval for the file reader to check if the log file has been updated
    public static int FILE_READER_CHECK_INTERVAL = 2;

    // Unit of the file reader check interval
    public static TimeUnit FILE_READER_CHECK_INTERVAL_UNIT = TimeUnit.MINUTES;

    // Current galactic position
    public static GalacticPosition GALACTIC_POSITION;
}