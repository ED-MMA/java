package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.events.Event;
import com.github.aklakina.edmma.machineInterface.FileReader;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;

public class FileDeleted extends Event {

    private static final Logger logger = LogManager.getLogger(FileDeleted.class);

    private final Path path;

    public FileDeleted(JSONObject event) {
        logger.info("FileDeleted event received");
        this.path = Path.of(event.getString("path"));
    }

    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        try {
            FileData f = Queries_.getFileDataByName(entityManager, path.getFileName().toString());
            logger.info("File " + path.getFileName().toString() + " was found in the database. Deleting it.");
            entityManager.getTransaction().begin();
            entityManager.remove(f);
            entityManager.getTransaction().commit();
        } catch (NoResultException e) {
            logger.debug("File " + path.getFileName().toString() + " was not found in the database.");
        }
        entityManager.close();
    }
}
