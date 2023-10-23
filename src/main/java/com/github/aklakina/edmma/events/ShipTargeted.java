package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Cluster;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class ShipTargeted extends Event {

    private static final Logger logger = LogManager.getLogger(ShipTargeted.class);

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
    }
    /*
    {
      "event":"ShipTargeted",
      "TargetLocked":true,
      "ScanStage":3,
      "Faction":"The Pilots Federation"
    }
     */
    private String faction;
    public ShipTargeted(JSONObject json) {
        logger.info("ShipTargeted event received");
        if (json.has("Faction"))
            faction = json.getString("Faction");
        else
            faction = "";
    }
}
