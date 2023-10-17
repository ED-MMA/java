package com.github.aklakina.edmma.databaseInterface;

public enum tableName {
    SOURCESYSTEMS("sourceSystems"),
    TARGETSYSTEMS("targetSystems"),
    STATIONS("stations"),
    FACTIONS("factions");

    private final String name;

    tableName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
