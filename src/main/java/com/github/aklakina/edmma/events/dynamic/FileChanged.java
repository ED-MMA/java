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

/**
 * FileChanged event:
 * <pre>
 *     {
 *         "event": "FileChanged"
 *         "path": "path/to/file"
 *     }
 * </pre>
 * Triggered when a file changes.
 */
public class FileChanged extends Event {

    private static final Logger logger = LogManager.getLogger(FileChanged.class);

    /**
     * The path of the file that changed.
     */
    private final Path path;

    /**
     * Constructor with parameters.
     * Initializes the path of the file that changed.
     *
     * @param event the JSON object containing the event data
     */
    public FileChanged(JSONObject event) {
        logger.info("FileChanged event received");
        this.path = Path.of(event.getString("path"));
    }

    /**
     * Processes the FileChanged event.
     * It gets the file data by its name, and if it does not exist, it creates it.
     * Then, it loads the file and processes the event.
     */
    @Override
    public void run() {
        logger.info("FileChanged event started processing");
        EntityManager entityManager = this.sessionFactory.createEntityManager();
        FileData fileData;
        try {
            fileData = Queries_.getFileDataByName(entityManager, path.getFileName().toString());
            logger.debug("Loaded file data from db. Last line read: " + fileData.getLastLineRead() + ", file size: " + fileData.getLastSize());
            fileData.loadFile();
        } catch (NoResultException e) {
            logger.debug("New file detected: " + path.getFileName().toString());
            fileData = new FileData(path.getFileName().toString(), 0, 0);
            entityManager.getTransaction().begin();
            entityManager.persist(fileData);
            entityManager.getTransaction().commit();
        }
        entityManager.close();
        SingletonFactory.getSingleton(FileReader.class).processEvent(fileData);
    }
}