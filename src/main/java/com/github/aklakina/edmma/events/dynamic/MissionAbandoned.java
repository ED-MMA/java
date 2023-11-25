package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * MissionAbandoned event:
 * <pre>
 * {
 *   "event": "MissionAbandoned",
 *   "MissionID": 726287843
 * }
 * </pre>
 * Triggered when the player abandons a mission.
 */
public class MissionAbandoned extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(MissionAbandoned.class);

    /**
     * The ID of the mission that was abandoned.
     */
    private final long missionId;

    /**
     * Constructor with parameters.
     * Initializes the ID of the mission that was abandoned.
     *
     * @param json the JSON object containing the event data
     */
    public MissionAbandoned(JSONObject json) {
        logger.info("MissionAbandoned event received");
        missionId = json.getLong("MissionID");
    }

    /**
     * Processes the MissionAbandoned event.
     * It gets the mission by its ID, and if it exists, it removes it.
     */
    @Override
    public void run() {
        logger.info("MissionAbandoned event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Mission mission;
        try {
            mission = Queries_.getMissionByID(entityManager, missionId);
            if (mission != null) {
                entityManager.getTransaction().begin();
                entityManager.remove(mission);
                entityManager.getTransaction().commit();
            }
        } catch (NoResultException e) {
            logger.error("Mission not found: " + missionId);
        }
        entityManager.close();
    }
}