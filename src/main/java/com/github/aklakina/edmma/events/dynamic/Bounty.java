package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Faction;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.events.Event;
import com.github.aklakina.edmma.logicalUnit.StatisticsCollector;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * spawner event:
 * <pre>
 * {
 *   "event":"Bounty",
 *   "VictimFaction":"The Pilots Federation",
 *   "TotalReward":0
 * }
 * </pre>
 * Triggered when the player kills a wanted ship.
 */
public class Bounty extends Event {

    /**
     * The logger object used for logging.
     */
    private static final Logger logger = LogManager.getLogger(Bounty.class);

    /**
     * The victim faction of the bounty event.
     */
    private final String VictimFaction;

    /**
     * Constructor with parameters.
     * Initializes the victim faction of the bounty event.
     *
     * @param json the JSON object containing the event data
     */
    public Bounty(JSONObject json) {
        logger.debug("Bounty event received");
        VictimFaction = json.getString("VictimFaction");
    }

    /**
     * Processes the bounty event and updates the progress of missions.
     * It gets the missions for the victim faction, and for each mission, it increases the progress by 1.
     * If a mission is completed, it logs the completion of the mission.
     */
    @Override
    public void run() {
        logger.info("Bounty event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();

        List<Mission> missions = Queries_.getMissionByTargetFaction(entityManager, VictimFaction);
        Set<Faction> factions = new HashSet<>();
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        boolean missionCompleted = false;
        for (Mission mission : missions) {
            if (factions.add(mission.getSource().getFaction())) {
                if (mission.isCompleted()) {
                    logger.error("Mission " + mission.getID() + " should not be completed. Data inconsistency.");
                }
                mission.setProgress(mission.getProgress() + 1);
                mission.setKillsLeft(mission.getKillsLeft() - 1);
                if (mission.getProgress() >= mission.getKillsRequired()) {
                    mission.setCompleted(true);
                }
                missionCompleted |= mission.isCompleted();
                entityManager.merge(mission);
                if (mission.isCompleted()) {
                    logger.info("Mission " + mission.getID() + " completed");
                }
            }
        }
        try {
            transaction.commit();
            if (missionCompleted) {
                SingletonFactory.getSingleton(StatisticsCollector.class).notifyCollector(new StatisticsCollector.StatisticsFlag[]{
                        StatisticsCollector.StatisticsFlag.MISSIONS,
                        StatisticsCollector.StatisticsFlag.COMPLETED
                });
            } else {
                SingletonFactory.getSingleton(StatisticsCollector.class).notifyCollector(new StatisticsCollector.StatisticsFlag[]{
                        StatisticsCollector.StatisticsFlag.MISSIONS
                });
            }
        } catch (Exception e) {
            logger.debug("Error committing transaction");
            logger.error("Error: " + e.getMessage());
            logger.trace(e.getStackTrace());
            transaction.rollback();
        }
        entityManager.close();
    }
}
