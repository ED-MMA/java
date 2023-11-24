package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.System;
import com.github.aklakina.edmma.database.orms.*;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MissionAccepted extends Event {

    private static final Logger logger = LogManager.getLogger(MissionAccepted.class);

    /*{ "timestamp":"2021-03-10T14:58:54Z"
     *  , "event":"MissionAccepted"
     *  , "Faction":"People's MET 20 Liberals"
     *  , "Name":"Mission_MassacreWing"
     *  , "LocalisedName":"Massacre Qi Yomisii Council faction Pirates"
     *  , "TargetType":"$MissionUtil_FactionTag_Pirate;"
     *  , "TargetType_Localised":"Pirates"
     *  , "TargetFaction":"Qi Yomisii Council"
     *  , "KillCount":20
     *  , "DestinationSystem":"Qi Yomisii"
     *  , "DestinationStation":"Seddon Terminal"
     *  , "Expiry":"2021-03-17T14:26:09Z"
     *  , "Wing":true
     *  , "Influence":"++"
     *  , "Reputation":"++"
     *  , "Reward":2428060
     *  , "MissionID":726287843 }
     */
    boolean massacre;
    Long missionID;
    String sourceFaction;
    String sourceStation;
    String targetFaction;
    String targetSystem;
    String expiry;
    boolean winged;
    double reward;
    Integer killsRequired;

    public MissionAccepted(JSONObject json) {
        logger.info("MissionAccepted event received");
        missionID = json.getLong("MissionID");
        sourceFaction = json.getString("Faction");
        targetFaction = json.getString("TargetFaction");
        targetSystem = json.getString("DestinationSystem");
        sourceStation = json.getString("DestinationStation");
        expiry = json.getString("Expiry");
        winged = json.getBoolean("Wing");
        reward = json.getDouble("Reward") / 1000000.0;
        killsRequired = json.getInt("KillCount");
        massacre = json.getString("Name").contains("Massacre");
    }

    @Override
    public void run() {
        logger.info("MissionAccepted event started processing");
        if (!massacre) {
            logger.debug("Not a massacre mission. Ignoring.");
            return;
        }
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Mission mission;
        Faction targetFaction;
        Faction sourceFaction;
        Station sourceStation;
        System targetSystem;
        Cluster cluster;
        MissionSource missionSource;
        GalacticPosition pos = Globals.GALACTIC_POSITION;

        if (pos.getSystem() == null) {
            logger.error("No sourceSystem found. Data inconsistency.");
            return;
        }

        try {
            mission = Queries_.getMissionByID(entityManager, missionID);
            if (mission != null) {
                logger.error("Mission " + missionID + " already exists");
                return;
            }
        } catch (NoResultException e) {
            logger.debug("Mission " + missionID + " not found in db. Creating new.");
        }

        try {
            targetFaction = Queries_.getFactionByName(entityManager, this.targetFaction);
            logger.debug("Faction " + this.targetFaction + " found in db.");
        } catch (NoResultException e) {
            logger.debug("Faction " + this.targetFaction + " not found in db. Creating new.");
            targetFaction = new Faction();
            targetFaction.setName(this.targetFaction);
        }

        try {
            sourceFaction = Queries_.getFactionByName(entityManager, this.sourceFaction);
            logger.debug("Faction " + this.sourceFaction + " found in db.");
        } catch (NoResultException e) {
            logger.debug("Faction " + this.sourceFaction + " not found in db. Creating new.");
            sourceFaction = new Faction();
            sourceFaction.setName(this.sourceFaction);
        }

        try {
            sourceStation = Queries_.getStationByName(entityManager, this.sourceStation);
            logger.debug("Station " + this.sourceStation + " found in db.");
        } catch (NoResultException e) {
            logger.debug("Station " + this.sourceStation + " not found in db. Creating new.");
            sourceStation = new Station();
            sourceStation.setSystem(pos.getSystem());
            sourceStation.setName(this.sourceStation);
        }

        try {
            targetSystem = Queries_.getSystemByName(entityManager, this.targetSystem);
            logger.debug("System " + this.targetSystem + " found in db.");
        } catch (NoResultException e) {
            logger.debug("System " + this.targetSystem + " not found in db. Creating new.");
            targetSystem = new System();
            targetSystem.setName(this.targetSystem);
        }

        try {
            cluster = Queries_.getCluster(entityManager, this.targetFaction, this.targetSystem);
            logger.debug("Cluster " + this.targetFaction + " - " + this.targetSystem + " found in db.");
        } catch (NoResultException e) {
            logger.debug("Cluster " + this.targetFaction + " - " + this.targetSystem + " not found in db. Creating new.");
            cluster = new Cluster();
            cluster.setTargetFaction(targetFaction);
            cluster.setTargetSystem(targetSystem);
        }

        try {
            missionSource = Queries_.getMissionSource(entityManager, this.sourceFaction, this.sourceStation, cluster.getID());
            logger.debug("MissionSource " + this.sourceFaction + " - " + this.sourceStation + " - " + cluster.getID() + " found in db.");
        } catch (NoResultException e) {
            logger.debug("MissionSource " + this.sourceFaction + " - " + this.sourceStation + " - " + cluster.getID() + " not found in db. Creating new.");
            missionSource = new MissionSource();
            missionSource.setFaction(sourceFaction);
            missionSource.setStation(sourceStation);
            missionSource.setCluster(cluster);
        }
        logger.debug("Building new mission");
        mission = new Mission();
        mission.setID(missionID);
        mission.setAcceptTime(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        mission.setExpiry(expiry);
        mission.setReward(reward);
        mission.setCluster(cluster);
        mission.setSource(missionSource);
        mission.setShareable(winged);
        mission.setKillsRequired(killsRequired);
        mission.setProgress(0);

        entityManager.getTransaction().begin();
        logger.debug("Persisting mission");
        entityManager.persist(targetFaction);
        entityManager.persist(sourceStation);
        entityManager.persist(targetSystem);
        entityManager.persist(sourceFaction);
        entityManager.persist(cluster);
        entityManager.persist(missionSource);
        entityManager.persist(mission);
        logger.debug("Persisting mission done");
        entityManager.getTransaction().commit();

        entityManager.close();

    }
}
