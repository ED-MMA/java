package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Faction;
import com.github.aklakina.edmma.database.orms.Mission;
import jakarta.persistence.EntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bounty extends Event {

    private static final Logger logger = LogManager.getLogger(Bounty.class);

    @Override
    public void run() {
        logger.info("Bounty event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        List<Mission> missions = Queries_.getMissionByTargetFaction(entityManager, VictimFaction);
        Set<Faction> factions = new HashSet<>();
        entityManager.getTransaction().begin();
        for (Mission mission : missions) {
            if (factions.add(mission.getSource().getFaction())) {
                if (mission.isCompleted()) {
                    logger.error("Mission " + mission.getID() + " should not be completed. Data inconsistency.");
                }
                mission.setProgress(mission.getProgress() + 1);
                entityManager.merge(mission);
            }
        }
        entityManager.getTransaction().commit();
    }
    /*
    {
      "event":"Bounty",
      "VictimFaction":"The Pilots Federation",
      "TotalReward":0
    }
     */

    private final String VictimFaction;

    public Bounty(JSONObject json) {
        logger.debug("Bounty event received");
        VictimFaction = json.getString("VictimFaction");
    }
}
