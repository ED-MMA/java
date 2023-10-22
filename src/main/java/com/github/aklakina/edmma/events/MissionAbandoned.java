package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.logicalUnit.DataFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.json.JSONObject;

public class MissionAbandoned extends Event {
    /*
    {
      "event": "MissionAbandoned",
      "MissionID": 726287843
    }
     */

    private final long missionId;

    public MissionAbandoned(JSONObject json) {
        missionId = json.getLong("MissionID");
    }

    @Override
    public void run() {

        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Mission mission;
        try {
            mission = Queries_.getMissionByID(entityManager, missionId);
            if (mission != null) {
                entityManager.remove(mission);
            }
        } catch (NoResultException e) {
            System.err.println("Mission not found: " + missionId);
        }
        entityManager.close();
    }
}
