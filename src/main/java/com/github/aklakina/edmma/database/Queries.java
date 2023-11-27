package com.github.aklakina.edmma.database;

import com.github.aklakina.edmma.database.orms.System;
import com.github.aklakina.edmma.database.orms.*;
import org.hibernate.annotations.processing.HQL;

import java.util.List;

/**
 * The Queries interface provides methods for executing HQL queries.
 * Each method corresponds to a specific query and returns the result of that query.
 * The parameters of the methods are used to parameterize the queries.
 */
public interface Queries {
    /**
     * Returns the cluster with the specified id
     *
     * @param id the id of the cluster
     *
     * @return the cluster with the specified id
     */
    @HQL("from Cluster where ID = :id")
    Cluster getClusterById(Long id);

    /**
     * Returns the cluster with the most missions for a given system.
     *
     * @param systemName the name of the system
     * @return the cluster with the most missions for the given system
     */
    @HQL("SELECT c " +
            "FROM Cluster c " +
            "WHERE c IN (SELECT clusters FROM System s WHERE s.name = :systemName) " +
            "ORDER BY SIZE(c.missions) DESC " +
            "FETCH FIRST 1 ROW ONLY")
    Cluster getClusterBySystemName(String systemName);

    /**
     * Returns the cluster with the most missions for a given system and target faction.
     *
     * @param systemName the name of the system
     * @param targetFactionName the name of the target faction
     * @return the cluster with the most missions for the given system and target faction
     */
    @HQL("SELECT c " +
            "FROM Cluster c " +
            "WHERE c IN (SELECT clusters FROM System s WHERE s.name = :systemName) " +
            "AND c.targetFaction.name = :targetFactionName " +
            "ORDER BY SIZE(c.missions) DESC")
    Cluster getClusterBySystemNameAnTargetName(String systemName, String targetFactionName);

    /**
     * Returns the file data for a given file name.
     *
     * @param name the name of the file
     * @return the file data for the given file name
     */
    @HQL("from FileData where name = :name")
    FileData getFileDataByName(String name);

    /**
     * Returns the galactic position for a given ID.
     *
     * @param id the ID of the galactic position
     * @return the galactic position for the given ID
     */
    @HQL("from GalacticPosition where ID = :id")
    GalacticPosition getGalacticPosition(Long id);

    /**
     * Returns the system for a given system name.
     *
     * @param name the name of the system
     * @return the system for the given system name
     */
    @HQL("from System where name = :name")
    System getSystemByName(String name);

    /**
     * Returns the station for a given station name.
     *
     * @param name the name of the station
     * @return the station for the given station name
     */
    @HQL("from Station where name = :name")
    Station getStationByName(String name);

    /**
     * Returns the mission for a given mission ID.
     *
     * @param id the ID of the mission
     * @return the mission for the given mission ID
     */
    @HQL("from Mission where ID = :id")
    Mission getMissionByID(Long id);

    /**
     * Returns the mission source for a given faction name, station name, and cluster ID.
     *
     * @param factionName the name of the faction
     * @param stationName the name of the station
     * @param clusterId the ID of the cluster
     * @return the mission source for the given faction name, station name, and cluster ID
     */
    @HQL("from MissionSource where faction.name = :factionName and station.name = :stationName and cluster.ID = :clusterId")
    MissionSource getMissionSource(String factionName, String stationName, Long clusterId);

    /**
     * Returns the cluster for a given target faction name and target system name.
     *
     * @param targetFactionName the name of the target faction
     * @param targetSystemName the name of the target system
     * @return the cluster for the given target faction name and target system name
     */
    @HQL("from Cluster where targetFaction.name = :targetFactionName and targetSystem.name = :targetSystemName")
    Cluster getCluster(String targetFactionName, String targetSystemName);

    /**
     * Returns the faction for a given faction name.
     *
     * @param name the name of the faction
     * @return the faction for the given faction name
     */
    @HQL("from Faction where name = :name")
    Faction getFactionByName(String name);

    /**
     * Returns a list of missions for a given target faction name where the progress is less than the kills required.
     * The missions are ordered by ID in ascending order.
     *
     * @param targetFactionName the name of the target faction
     * @return a list of missions for the given target faction name where the progress is less than the kills required
     */
    @HQL("FROM Mission " +
            "WHERE cluster.targetFaction.name = :targetFactionName AND progress < killsRequired " +
            "ORDER BY ID ASC")
    List<Mission> getMissionByTargetFaction(String targetFactionName);

    /**
     * Returns a list of missions for a given source faction name where the progress is less than the kills required.
     * The missions are ordered by ID in ascending order.
     *
     * @param sourceFactionName the name of the source faction
     * @return a list of missions for the given source faction name where the progress is less than the kills required
     */
    @HQL("FROM Mission " +
            "WHERE source.faction.name = :sourceFactionName AND progress < killsRequired " +
            "ORDER BY ID ASC")
    List<Mission> getMissionBySourceFaction(String sourceFactionName);
}