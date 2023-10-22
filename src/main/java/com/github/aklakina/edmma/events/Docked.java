package com.github.aklakina.edmma.events;


import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.database.orms.Station;
import com.github.aklakina.edmma.database.orms.System;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.hibernate.annotations.FlushModeType;
import org.json.JSONObject;

public class Docked extends Event {
    /*
     *{ "timestamp":"2021-03-14T18:56:42Z"
     *, "event":"Docked"
     *, "StationName":"XNB-55Z"
     *, "StationType":"FleetCarrier"
     *, "StarSystem":"Qi Yomisii"
     *, "SystemAddress":22953915599624
     *, "MarketID":3705556992
     *, "StationFaction":{ "Name":"FleetCarrier" }
     *, "StationGovernment":"$government_Carrier;"
     *, "StationGovernment_Localised":"Private Ownership "
     *, "StationServices":[ "dock", "autodock", "commodities", "contacts", "exploration", "outfitting", "crewlounge", "rearm", "refuel", "repair", "shipyard", "engineer", "flightcontroller", "stationoperations", "stationMenu", "carriermanagement", "carrierfuel", "voucherredemption" ]
     *, "StationEconomy":"$economy_Carrier;"
     *, "StationEconomy_Localised":"Private Enterprise"
     *, "StationEconomies":[ { "Name":"$economy_Carrier;", "Name_Localised":"Private Enterprise", "Proportion":1.000000 } ]
     *, "DistFromStarLS":610.551176 }
     */

    private final String StationName;
    private final String SystemName;

    public Docked(JSONObject json) {
        StationName = json.getString("StationName");
        SystemName = json.getString("StarSystem");
    }

    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        System system;
        Station station;
        GalacticPosition pos = Globals.GALACTIC_POSITION;
        try {
            system = Queries_.getSystemByName(entityManager, SystemName);
        } catch (NoResultException e) {
            system = new System();
            system.setName(SystemName);
            entityManager.persist(system);
        }

        try {
            station = Queries_.getStationByName(entityManager, StationName);
        } catch (NoResultException e) {
            station = new Station();
            station.setName(StationName);
            station.setSystem(system);
            entityManager.persist(station);
        }

        if (!system.equals(pos.getSystem()) || !station.equals(pos.getStation())) {
            pos.setSystem(system);
            pos.setStation(station);
            entityManager.merge(pos);
        }

        try {
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (Exception e) {
            java.lang.System.out.println("Error during transaction closing: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
