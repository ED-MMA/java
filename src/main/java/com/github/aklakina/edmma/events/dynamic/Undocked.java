package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * Undocked event:
 * <pre>
 * {
 *   "timestamp":"2021-03-14T18:59:39Z",
 *   "event":"Undocked",
 *   "StationName":"XNB-55Z",
 *   "StationType":"FleetCarrier",
 *   "MarketID":3705556992
 * }
 * </pre>
 * Triggered when the player undocks from a station.
 */
public class Undocked extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(Undocked.class);

    /**
     * Constructor with parameters.
     * Initializes the event data.
     *
     * @param json the JSON object containing the event data
     */
    public Undocked(JSONObject json) {
        logger.info("Undocked event received");
    }

    /**
     * Processes the Undocked event.
     * It updates the galactic position to indicate that the player is not docked at any station.
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
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(pos);
        try {
            transaction.commit();
        } catch (Exception e) {
            logger.debug("Error while updating galactic position. Rolling back.");
            logger.error("Error: ", e);
            logger.trace(e.getStackTrace());
            transaction.rollback();
        }
        entityManager.close();
    }
}