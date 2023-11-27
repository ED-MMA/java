package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.events.Event;
import com.github.aklakina.edmma.logicalUnit.StatisticsCollector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * MissionRedirected event:
 * <pre>
 * {
 *   "timestamp":"2021-03-14T18:10:43Z",
 *   "event":"MissionRedirected",
 *   "MissionID":726287843,
 *   "Name":"Mission_MassacreWing",
 *   "NewDestinationStation":"Tesla Terminal",
 *   "NewDestinationSystem":"Njulngan",
 *   "OldDestinationStation":"",
 *   "OldDestinationSystem":"Qi Yomisii"
 * }
 * </pre>
 * Triggered when the player's mission is redirected.
 */
public class MissionRedirected extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(MissionRedirected.class);

    /**
     * The ID of the mission that was redirected.
     */
    private final Long missionID;

    /**
     * Constructor with parameters.
     * Initializes the ID of the mission that was redirected.
     *
     * @param json the JSON object containing the event data
     */
    public MissionRedirected(JSONObject json) {
        logger.info("MissionRedirected event received");
        missionID = json.getLong("MissionID");
    }

    /**
     * Processes the MissionRedirected event.
     * It gets the mission by its ID, and if it exists, it updates the mission progress.
     */
    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Mission mission;
        try {
            mission = Queries_.getMissionByID(entityManager, missionID);
            logger.debug("Loaded mission from db. ID: " + mission.getID() + ", kills required: " + mission.getKillsRequired());
        } catch (NoResultException e) {
            logger.error("Mission not found: " + missionID);
            return;
        }
        if (!mission.isCompleted()) {
            logger.error("Mission " + missionID + " should be completed. Data inconsistency.");
        }
        mission.setProgress(mission.getKillsRequired());
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.merge(mission);
        try {
            transaction.commit();
            SingletonFactory.getSingleton(StatisticsCollector.class).notifyCollector(new StatisticsCollector.StatisticsFlag[]{
                    StatisticsCollector.StatisticsFlag.COMPLETED
            });
        } catch (Exception e) {
            logger.error("Error during transaction closing: ", e);
            logger.trace(e.getStackTrace());
            transaction.rollback();
        }
        entityManager.close();
    }
}