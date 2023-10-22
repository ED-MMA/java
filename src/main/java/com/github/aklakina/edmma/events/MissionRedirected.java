package com.github.aklakina.edmma.events;

import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Mission;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.json.JSONObject;

public class MissionRedirected extends Event {
    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        Mission mission;
        try {
            mission = Queries_.getMissionByID(entityManager, missionID);
        } catch (NoResultException e) {
            java.lang.System.err.println("Mission " + missionID + " does not exist");
            return;
        }
        if (!mission.isCompleted()) {
            java.lang.System.err.println("Mission " + missionID + " should not be completed. Data inconsistency.");
        }
        mission.setProgress(mission.getKillsRequired());
        entityManager.getTransaction().begin();
        entityManager.merge(mission);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    /*{ "timestamp":"2021-03-14T18:10:43Z"
     *  , "event":"MissionRedirected"
     *  , "MissionID":726287843
     *  , "Name":"Mission_MassacreWing"
     *  , "NewDestinationStation":"Tesla Terminal"
     *  , "NewDestinationSystem":"Njulngan"
     *  , "OldDestinationStation":""
     *  , "OldDestinationSystem":"Qi Yomisii" }
     */

    Long missionID;

    public MissionRedirected(JSONObject json) {
        missionID = json.getLong("MissionID");
    }
}
