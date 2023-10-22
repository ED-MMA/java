package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;

public class Undocked extends Event {
    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        GalacticPosition pos = Globals.GALACTIC_POSITION;
        if (pos.getStation() == null) {
            System.err.println("Unkown system position. Data inconsistency.");
            return;
        }
        pos.setStation(null);
        entityManager.getTransaction().begin();
        entityManager.merge(pos);
        entityManager.getTransaction().commit();
    }
    /*{ "timestamp":"2021-03-14T18:59:39Z"
     *  , "event":"Undocked"
     *  , "StationName":"XNB-55Z"
     *  , "StationType":"FleetCarrier"
     *  , "MarketID":3705556992 }
     */

    public Undocked(JSONObject json) {

    }
}
