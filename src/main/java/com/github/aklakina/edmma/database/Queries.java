package com.github.aklakina.edmma.database;

import com.github.aklakina.edmma.database.orms.*;
import com.github.aklakina.edmma.database.orms.System;
import org.hibernate.annotations.processing.HQL;

import java.util.List;

public interface Queries {
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
}
