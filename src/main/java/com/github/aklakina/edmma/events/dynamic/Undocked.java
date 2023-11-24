package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Undocked extends Event {

    private static final Logger logger = LogManager.getLogger(Undocked.class);

    public Undocked(JSONObject json) {
        logger.info("Undocked event received");
    }
    /*{ "timestamp":"2021-03-14T18:59:39Z"
     *  , "event":"Undocked"
     *  , "StationName":"XNB-55Z"
     *  , "StationType":"FleetCarrier"
     *  , "MarketID":3705556992 }
     */

    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        GalacticPosition pos = Globals.GALACTIC_POSITION;
        if (pos.getStation() == null) {
            logger.error("Unkown system position. Data inconsistency.");
            return;
        }
        pos.setStation(null);
        entityManager.getTransaction().begin();
        entityManager.merge(pos);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}
