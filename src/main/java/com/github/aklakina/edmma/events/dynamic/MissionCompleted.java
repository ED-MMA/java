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
 * MissionCompleted event:
 * <pre>
 * {
 *   "event":"MissionCompleted",
 *   "Name":"Massacre Qi Yomisii Council faction Pirates",
 *   "MissionID":726287843
 * }
 * </pre>
 * Triggered when the player completes a mission.
 */
public class MissionCompleted extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(MissionCompleted.class);

    /**
     * Indicates if the mission is a massacre mission.
     */
    private final boolean massacre;

    /**
     * The ID of the mission that was completed.
     */
    private final Long missionID;

    /**
     * Constructor with parameters.
     * Initializes the mission type and ID.
     *
     * @param json the JSON object containing the event data
     */
    public MissionCompleted(JSONObject json) {
        logger.info("MissionCompleted event received");
        this.massacre = json.getString("Name").contains("Massacre");
        this.missionID = json.getLong("MissionID");
    }

    /**
     * Processes the MissionCompleted event.
     * If the mission is a massacre mission, it gets the mission by its ID, and if it exists, it removes it.
     */
    @Override
    public void run() {
        logger.info("MissionCompleted event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        entityManager.getTransaction().begin();
        if (massacre) {
            try {
                Mission m = Queries_.getMissionByID(entityManager, missionID);
                entityManager.remove(m);
            } catch (NoResultException e) {
                logger.error("Mission not found in db");
                logger.trace(e.getStackTrace());
            }
        }
        entityManager.getTransaction().commit();
        entityManager.close();
    }
}