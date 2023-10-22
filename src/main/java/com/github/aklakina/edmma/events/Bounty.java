package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Faction;
import com.github.aklakina.edmma.database.orms.Mission;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bounty extends Event {
    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        List<Mission> missions = Queries_.getMissionByTargetFaction(entityManager, VictimFaction);
        Set<Faction> factions = new HashSet<>();
        entityManager.getTransaction().begin();
        for (Mission mission : missions) {
            if (factions.add(mission.getSource().getFaction())) {
                if (mission.isCompleted()) {
                    java.lang.System.err.println("Mission " + mission.getID() + " should not be completed. Data inconsistency.");
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
        VictimFaction = json.getString("VictimFaction");
    }
}
