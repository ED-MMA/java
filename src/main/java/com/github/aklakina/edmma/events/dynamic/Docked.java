package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.database.orms.Station;
import com.github.aklakina.edmma.database.orms.System;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * Docked event:
 * <pre>
 * {
 *   "timestamp":"2021-03-14T18:56:42Z",
 *   "event":"Docked",
 *   "StationName":"XNB-55Z",
 *   "StationType":"FleetCarrier",
 *   "StarSystem":"Qi Yomisii",
 *   "SystemAddress":22953915599624,
 *   "MarketID":3705556992,
 *   "StationFaction":{ "Name":"FleetCarrier" },
 *   "StationGovernment":"$government_Carrier;",
 *   "StationGovernment_Localised":"Private Ownership ",
 *   "StationServices":[ "dock", "autodock", "commodities", "contacts", "exploration", "outfitting", "crewlounge", "rearm", "refuel", "repair", "shipyard", "engineer", "flightcontroller", "stationoperations", "stationMenu", "carriermanagement", "carrierfuel", "voucherredemption" ],
 *   "StationEconomy":"$economy_Carrier;",
 *   "StationEconomy_Localised":"Private Enterprise",
 *   "StationEconomies":[ { "Name":"$economy_Carrier;", "Name_Localised":"Private Enterprise", "Proportion":1.000000 } ],
 *   "DistFromStarLS":610.551176
 * }
 * </pre>
 * Triggered when the player docks at a station.
 */
public class Docked extends Event {

    private static final Logger logger = LogManager.getLogger(Docked.class);

    /**
     * The name of the station where the player docked.
     */
    private final String StationName;

    /**
     * The name of the system where the player docked.
     */
    private final String SystemName;

    /**
     * Constructor with parameters.
     * Initializes the station name and system name where the player docked.
     *
     * @param json the JSON object containing the event data
     */
    public Docked(JSONObject json) {
        logger.info("Docked event received");
        StationName = json.getString("StationName");
        SystemName = json.getString("StarSystem");
    }

    /**
     * Processes the docked event and updates the galactic position.
     * It gets the system and station by their names, and if they do not exist, it creates them.
     * Then, it updates the galactic position to the new system and station.
     */
    @Override
    public void run() {
        logger.info("Docked event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        System system;
        Station station;
        GalacticPosition pos = Globals.GALACTIC_POSITION;
        try {
            system = Queries_.getSystemByName(entityManager, SystemName);
        } catch (NoResultException e) {
            logger.debug("System " + SystemName + " not found in db. Creating new.");
            system = new System();
            system.setName(SystemName);
            entityManager.persist(system);
        }

        try {
            station = Queries_.getStationByName(entityManager, StationName);
        } catch (NoResultException e) {
            logger.debug("Station " + StationName + " not found in db. Creating new.");
            station = new Station();
            station.setName(StationName);
            station.setSystem(system);
            entityManager.persist(station);
        }

        if (!system.equals(pos.getSystem()) || !station.equals(pos.getStation())) {
            logger.debug("Updating galactic position to system: " + system.getName() + " | station: " + station.getName());
            pos.setSystem(system);
            pos.setStation(station);
            entityManager.merge(pos);
        }

        try {
            transaction.commit();
            entityManager.close();
        } catch (Exception e) {
            logger.error("Error during transaction closing: ", e);
            logger.trace(e.getStackTrace());
            transaction.rollback();
        }
    }

}