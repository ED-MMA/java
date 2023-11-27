package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.database.orms.System;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * FSDJump event:
 * <pre>
 * {
 *   "timestamp":"2022-04-21T20:05:12Z",
 *   "event":"FSDJump",
 *   "StarSystem":"Pegasi Sector XU-O b6-4",
 *   "SystemAddress":9465168209329,
 *   "StarPos":[-129.56250,-75.81250,15.78125],
 *   "SystemAllegiance":"",
 *   "SystemEconomy":"$economy_None;",
 *   "SystemEconomy_Localised":"None",
 *   "SystemSecondEconomy":"$economy_None;",
 *   "SystemSecondEconomy_Localised":"None",
 *   "SystemGovernment":"$government_None;",
 *   "SystemGovernment_Localised":"None",
 *   "SystemSecurity":"$GAlAXY_MAP_INFO_state_anarchy;",
 *   "SystemSecurity_Localised":"Anarchy",
 *   "Population":0,
 *   "Body":"Pegasi Sector XU-O b6-4",
 *   "BodyID":0,
 *   "BodyType":"Star",
 *   "JumpDist":19.757,
 *   "FuelUsed":6.710013,
 *   "FuelLevel":25.289986
 * }
 * </pre>
 * Triggered when the player makes a jump to another star system.
 */
public class FSDJump extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(FSDJump.class);

    /**
     * The name of the star system where the player jumped to.
     */
    private final String StarSystem;

    /**
     * Constructor with parameters.
     * Initializes the star system where the player jumped to.
     *
     * @param json the JSON object containing the event data
     */
    public FSDJump(JSONObject json) {
        logger.info("FSDJump event received");
        StarSystem = json.getString("StarSystem");
    }

    /**
     * Processes the FSDJump event and updates the galactic position.
     * It gets the system by its name, and if it does not exist, it creates it.
     * Then, it updates the galactic position to the new system.
     */
    @Override
    public void run() {
        logger.info("FSDJump event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        System sys;
        GalacticPosition pos = Globals.GALACTIC_POSITION;
        try {
            sys = Queries_.getSystemByName(entityManager, StarSystem);
        } catch (NoResultException e) {
            logger.debug("System " + StarSystem + " not found in db. Creating new.");
            sys = new System();
            sys.setName(StarSystem);
            entityManager.persist(sys);
        }

        if (pos.getSystem() == null || !pos.getSystem().equals(sys)) {
            logger.debug("Updating galactic position to system: " + sys.getName() + " | station: NULL");
            pos.setSystem(sys);
            pos.setStation(null);
            entityManager.merge(pos);
        }

        try {
            transaction.commit();
            entityManager.close();
        } catch (Exception e) {
            logger.error("Error during transaction closing: ", e);
            logger.trace(e.getStackTrace());
        }
    }
}