package com.github.aklakina.edmma.events.dynamic;

import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.FileData;
import com.github.aklakina.edmma.events.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Path;

/**
 * FileDeleted event:
 * <pre>
 *     {
 *         "event": "FileDeleted"
 *         "path": "path/to/file"
 *      }
 * </pre>
 * Triggered when a file is deleted.
 */
public class FileDeleted extends Event {

    private static final Logger logger = LogManager.getLogger(FileDeleted.class);

    /**
     * The path of the file that was deleted.
     */
    private final Path path;

    /**
     * Constructor with parameters.
     * Initializes the path of the file that was deleted.
     *
     * @param event the JSON object containing the event data
     */
    public FileDeleted(JSONObject event) {
        logger.info("FileDeleted event received");
        this.path = Path.of(event.getString("path"));
    }

    /**
     * Processes the FileDeleted event.
     * It gets the file data by its name, and if it exists, it deletes it.
     */
    @Override
    public void run() {
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            FileData f = Queries_.getFileDataByName(entityManager, path.getFileName().toString());
            logger.info("File " + path.getFileName().toString() + " was found in the database. Deleting it.");
            transaction.begin();
            entityManager.remove(f);
            transaction.commit();
        } catch (NoResultException e) {
            logger.debug("File " + path.getFileName().toString() + " was not found in the database.");
        }
        entityManager.close();
    }
}