package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.database.orms.System;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class FSDJump extends Event {

    private static final Logger logger = LogManager.getLogger(FSDJump.class);

    /* { "timestamp":"2022-04-21T20:05:12Z"
     * , "event":"FSDJump"
     * , "StarSystem":"Pegasi Sector XU-O b6-4"
     * , "SystemAddress":9465168209329
     * , "StarPos":[-129.56250,-75.81250,15.78125]
     * , "SystemAllegiance":""
     * , "SystemEconomy":"$economy_None;"
     * , "SystemEconomy_Localised":"None"
     * , "SystemSecondEconomy":"$economy_None;"
     * , "SystemSecondEconomy_Localised":"None"
     * , "SystemGovernment":"$government_None;"
     * , "SystemGovernment_Localised":"None"
     * , "SystemSecurity":"$GAlAXY_MAP_INFO_state_anarchy;"
     * , "SystemSecurity_Localised":"Anarchy"
     * , "Population":0
     * , "Body":"Pegasi Sector XU-O b6-4"
     * , "BodyID":0
     * , "BodyType":"Star"
     * , "JumpDist":19.757
     * , "FuelUsed":6.710013
     * , "FuelLevel":25.289986 }
     */

    private final String StarSystem;

    public FSDJump(JSONObject json) {
        logger.info("FSDJump event received");
        StarSystem = json.getString("StarSystem");
    }

    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
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

        entityManager.getTransaction().commit();
        entityManager.close();

    }
}
