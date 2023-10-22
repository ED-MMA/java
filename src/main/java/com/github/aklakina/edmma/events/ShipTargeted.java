package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Cluster;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.json.JSONObject;

public class ShipTargeted extends Event {
    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        GalacticPosition pos = Globals.GALACTIC_POSITION;
        if (pos.getSystem() == null) {
            System.err.println("Unkown system position. Data inconsistency.");
            return;
        }
        try {
            Queries_.getCluster(entityManager, faction, pos.getSystem().getName());
        } catch (NoResultException e) {
            System.out.println("Non-target faction. Ignoring.");
            return;
        }
        System.out.println("Target faction found. Ready to fire.");
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
        if (json.has("Faction"))
            faction = json.getString("Faction");
    }
}
