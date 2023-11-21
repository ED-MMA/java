package com.github.aklakina.edmma.database;

import com.github.aklakina.edmma.database.orms.*;
import com.github.aklakina.edmma.database.orms.System;
import org.hibernate.annotations.processing.HQL;

import java.util.HashMap;
import java.util.List;

public interface Queries {
    @HQL("SELECT c " +
            "FROM Cluster c " +
            "WHERE c IN (SELECT clusters FROM System s WHERE s.name = :systemName) " +
            "ORDER BY SIZE(c.missions) DESC "+
            "FETCH FIRST 1 ROW ONLY")
    Cluster getClusterBySystemName(String systemName);
    @HQL("SELECT c " +
            "FROM Cluster c " +
            "WHERE c IN (SELECT clusters FROM System s WHERE s.name = :systemName) " +
            "AND c.targetFaction.name = :targetFactionName " +
            "ORDER BY SIZE(c.missions) DESC")
    Cluster getClusterBySystemNameAnTargetName(String systemName, String targetFactionName);
    @HQL("from FileData where name = :name")
    FileData getFileDataByName(String name);
    @HQL("from GalacticPosition where ID = :id")
    GalacticPosition getGalacticPosition(Long id);
    @HQL("from System where name = :name")
    System getSystemByName(String name);
    @HQL("from Station where name = :name")
    Station getStationByName(String name);
    @HQL("from Mission where ID = :id")
    Mission getMissionByID(Long id);
    @HQL("from MissionSource where faction.name = :factionName and station.name = :stationName and cluster.ID = :clusterId")
    MissionSource getMissionSource(String factionName, String stationName, Long clusterId);
    @HQL("from Cluster where targetFaction.name = :targetFactionName and targetSystem.name = :targetSystemName")
    Cluster getCluster(String targetFactionName, String targetSystemName);
    @HQL("from Faction where name = :name")
    Faction getFactionByName(String name);
    @HQL("FROM Mission " +
            "WHERE cluster.targetFaction.name = :targetFactionName AND progress < killsRequired " +
            "ORDER BY ID ASC")
    List<Mission> getMissionByTargetFaction(String targetFactionName);
    @HQL("FROM Mission " +
            "WHERE source.faction.name = :sourceFactionName AND progress < killsRequired " +
            "ORDER BY ID ASC")
    List<Mission> getMissionBySourceFaction(String sourceFactionName);
}
