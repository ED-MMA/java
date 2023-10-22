package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Globals;
import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.database.Queries_;
import com.github.aklakina.edmma.database.orms.GalacticPosition;
import com.github.aklakina.edmma.machineInterface.WatchDir;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Singleton
public class Init {

    Thread fileWatcherThread;
    Thread eventHandlerThread;
    public Init() {
        System.out.println("Init");
        EntityManager entityManager = ORMConfig.sessionFactory.createEntityManager();
        try {
            Class.forName("com.github.aklakina.edmma.events.Event");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading events");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        try {
            Globals.GALACTIC_POSITION = Queries_.getGalacticPosition(entityManager, 0L);
        } catch (NoResultException e) {
            System.out.println("No position found. Creating new.");
            Globals.GALACTIC_POSITION = new GalacticPosition();
            entityManager.getTransaction().begin();
            entityManager.persist(Globals.GALACTIC_POSITION);
            entityManager.getTransaction().commit();
        }
        entityManager.close();


        // run the watchDir in a background thread
        fileWatcherThread = new Thread(SingletonFactory.getSingleton(WatchDir.class));
        fileWatcherThread.start();
        eventHandlerThread = new Thread(SingletonFactory.getSingleton(EventHandler.class));
        eventHandlerThread.start();
        /*TODO: Trigger fileChanged event on every file in the directory matching the regexp*/

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            SingletonFactory.getSingleton(AppCloser.class);
        }));

    }
}
