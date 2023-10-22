package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.*;
import com.github.aklakina.edmma.database.orms.System;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.json.JSONObject;

public class MissionAccepted extends Event {

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
        missionID = json.getLong("MissionID");
        sourceFaction = json.getString("Faction");
        targetFaction = json.getString("TargetFaction");
        targetSystem = json.getString("DestinationSystem");
        sourceStation = json.getString("DestinationStation");
        expiry = json.getString("Expiry");
        winged = json.getBoolean("Wing");
        reward = json.getDouble("Reward")/1000000.0;
        killsRequired = json.getInt("KillCount");
    }

    @Override
    public void run() {
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
            java.lang.System.err.println("No sourceSystem found. Data inconsistency.");
            return;
        }

        try {
            mission = Queries_.getMissionByID(entityManager, missionID);
            if (mission != null) {
                java.lang.System.err.println("Mission " + missionID + " already exists");
                return;
            }
        } catch (NoResultException e) {
            
        }
        
        try {
            targetFaction = Queries_.getFactionByName(entityManager, this.targetFaction);
        } catch (NoResultException e) {
            targetFaction = new Faction();
            targetFaction.setName(this.targetFaction);
        }
        
        try {
            sourceFaction = Queries_.getFactionByName(entityManager, this.sourceFaction);
        } catch (NoResultException e) {
            sourceFaction = new Faction();
            sourceFaction.setName(this.sourceFaction);
        }
        
        try {
            sourceStation = Queries_.getStationByName(entityManager, this.sourceStation);
        } catch (NoResultException e) {
            sourceStation = new Station();
            sourceStation.setSystem(pos.getSystem());
            sourceStation.setName(this.sourceStation);
        }
        
        try {
            targetSystem = Queries_.getSystemByName(entityManager, this.targetSystem);
        } catch (NoResultException e) {
            targetSystem = new System();
            targetSystem.setName(this.targetSystem);
        }
        
        try {
            cluster = Queries_.getCluster(entityManager, this.targetFaction, this.targetSystem);
        } catch (NoResultException e) {
            cluster = new Cluster();
            cluster.setTargetFaction(targetFaction);
            cluster.setTargetSystem(targetSystem);
        }
        
        try {
            missionSource = Queries_.getMissionSource(entityManager, this.sourceFaction, this.sourceStation, cluster.getID());
        } catch (NoResultException e) {
            missionSource = new MissionSource();
            missionSource.setFaction(sourceFaction);
            missionSource.setStation(sourceStation);
            missionSource.setCluster(cluster);
        }

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

        entityManager.persist(targetFaction);
        entityManager.persist(sourceStation);
        entityManager.persist(targetSystem);
        entityManager.persist(sourceFaction);
        entityManager.persist(cluster);
        entityManager.persist(missionSource);
        entityManager.persist(mission);

        entityManager.getTransaction().commit();

        entityManager.close();

    }
}
