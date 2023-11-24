package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Mission;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class MissionCompleted extends Event {

    private static final Logger logger = LogManager.getLogger(MissionCompleted.class);

    private final boolean massacre;
    private final Long missionID;

    public MissionCompleted(JSONObject json) {
        this.massacre=json.getString("Name").contains("Massacre");
        this.missionID=json.getLong("MissionID");
    }

    @Override
    public void run() {

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
