package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * ShipTargeted event:
 * <pre>
 * {
 *   "event":"ShipTargeted",
 *   "TargetLocked":true,
 *   "ScanStage":3,
 *   "Faction":"The Pilots Federation"
 * }
 * </pre>
 * Triggered when the player targets a ship.
 */
public class ShipTargeted extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(ShipTargeted.class);

    /**
     * The faction of the targeted ship.
     */
    private final String faction;

    /**
     * Constructor with parameters.
     * Initializes the faction of the targeted ship.
     *
     * @param json the JSON object containing the event data
     */
    public ShipTargeted(JSONObject json) {
        logger.info("ShipTargeted event received");
        if (json.has("Faction"))
            faction = json.getString("Faction");
        else
            faction = "";
    }

    /**
     * Processes the ShipTargeted event.
     * It checks if the targeted ship's faction is a target faction.
     * If it is, it logs that the ship is ready to fire.
     */
    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        GalacticPosition pos = Globals.GALACTIC_POSITION;
        if (pos.getSystem() == null) {
            logger.error("No system data. Data inconsistency.");
            return;
        }
        try {
            Queries_.getCluster(entityManager, faction, pos.getSystem().getName());
        } catch (NoResultException e) {
            logger.debug("Non-target faction. Ignoring.");
            return;
        }
        logger.debug("Target faction found. Ready to fire.");
        entityManager.close();
    }
}