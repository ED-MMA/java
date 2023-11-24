package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.Cluster;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.humanInterface.main_window;
import com.github.aklakina.edmma.logicalUnit.threading.CloserMethods;
import com.github.aklakina.edmma.logicalUnit.threading.RegisteredThread;
import com.github.aklakina.edmma.logicalUnit.threading.Threads;
import com.github.aklakina.edmma.machineInterface.WatchDir;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Singleton
public class Init {

    private static final Logger logger = LogManager.getLogger(Init.class);
    public boolean initState = true;

    public Init() {
    }

    public void start() {
        logger.info("Init class started");
        EntityManager entityManager = ORMConfig.sessionFactory.createEntityManager();
        try {
            Class.forName("com.github.aklakina.edmma.events.Event");
        } catch (ClassNotFoundException e) {
            logger.error("Event class not found");
            logger.trace(e.getStackTrace());
        }
        try {
            Globals.GALACTIC_POSITION = Queries_.getGalacticPosition(entityManager, 0L);
        } catch (NoResultException e) {
            logger.debug("No galactic position found in db. Creating new.");
            Globals.GALACTIC_POSITION = new GalacticPosition();
            entityManager.getTransaction().begin();
            entityManager.persist(Globals.GALACTIC_POSITION);
            entityManager.getTransaction().commit();
        }
        entityManager.close();

        RegisteredThread thread = new RegisteredThread(SingletonFactory.getSingleton(EventHandler.class), CloserMethods.INTERRUPT);
        thread.setName("EventHandler");
        thread.start();

        processChanges();

        initState = false;

        // run the watchDir in a background thread
        thread = new RegisteredThread(SingletonFactory.getSingleton(WatchDir.class), CloserMethods.INTERRUPT);
        thread.setName("WatchDir");
        thread.start();

        SingletonFactory.getSingleton(StatisticsCollector.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> SingletonFactory.getSingleton(AppCloser.class).close()));
    }

    private void processChanges() {
        File[] files = Paths.get(Globals.ELITE_LOG_HOME).toFile().listFiles((dir, name) -> name.matches(Globals.LOG_FILE_NAME_REGEX.pattern()));
        if (files == null) {
            logger.error("No files found");
            return;
        }
        List<File> logs = new ArrayList<>(List.of(files));
        logs.sort(Comparator.comparing(File::getName));
        logger.debug("Found files: " + logs);
        for (File file : logs) {
            JSONObject data = new JSONObject()
                    .put("event", "FileChanged")
                    .put("path", file.getPath());
            logger.debug("Spawning event: " + data);
            SingletonFactory.getSingleton(DataFactory.class).spawnEvent(data);
            synchronized (this) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    logger.trace(e.getStackTrace());
                }
            }
        }
    }

}
