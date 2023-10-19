package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
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

    private String StationName;
    private String SystemName;

    public Docked(JSONObject json) {
        StationName = json.getString("StationName");
        SystemName = json.getString("StarSystem");
    }
    @Override
    public void run() {

    }
}
