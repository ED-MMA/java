package com.github.aklakina.edmma.base;

import com.github.aklakina.edmma.database.orms.Station;

import java.util.regex.Pattern;

public class Globals {
    private Globals() {
    }

    public static final String DATABASE_NAME = "EDMMA.sqlite";
    public static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_NAME;
    public static final String DATABASE_DRIVER = "org.sqlite.JDBC";
    public static final String USER_HOME = java.lang.System.getProperty("user.home");
    public static final String ELITE_LOG_HOME = USER_HOME + "/Saved Games/Frontier Developments/Elite Dangerous";
    public static final Pattern LOG_FILE_NAME_REGEX = Pattern.compile("Journal.*\\.log");

    public static class GalacticPosition {
        public System sourceSystem;
        public Station station;
    }
}
