package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Mission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MissionAbandoned extends Event {

    private static final Logger logger = LogManager.getLogger(MissionAbandoned.class);

    /*
    {
      "event": "MissionAbandoned",
      "MissionID": 726287843
    }
     */

    private final long missionId;

    public MissionAbandoned(JSONObject json) {
        logger.info("MissionAbandoned event received");
        missionId = json.getLong("MissionID");
    }

    @Override
    public void run() {
        logger.info("MissionAbandoned event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Mission mission;
        try {
            mission = Queries_.getMissionByID(entityManager, missionId);
            if (mission != null) {
                entityManager.remove(mission);
            }
        } catch (NoResultException e) {
            logger.error("Mission not found: " + missionId);
        }
        entityManager.close();
    }
}
