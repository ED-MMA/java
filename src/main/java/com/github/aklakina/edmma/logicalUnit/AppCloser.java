package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.machineInterface.FileReader;
import com.github.aklakina.edmma.machineInterface.WatchDir;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;

@Singleton
public class AppCloser {

    private static final Logger logger = LogManager.getLogger(AppCloser.class);

    public AppCloser() {
        logger.debug("AppCloser");
        SingletonFactory.getSingleton(EventHandler.class).shouldExit = true;
        Thread fileWatcherThread = SingletonFactory.getSingleton(Init.class).fileWatcherThread;
        Thread eventHandlerThread = SingletonFactory.getSingleton(Init.class).eventHandlerThread;
        try {
            SingletonFactory.getSingleton(WatchDir.class).shouldExit = true;
            SingletonFactory.getSingleton(EventHandler.class).shouldExit = true;
            eventHandlerThread.interrupt();
            fileWatcherThread.interrupt();
            fileWatcherThread.join();
            eventHandlerThread.join();
        } catch (InterruptedException e) {
            logger.error("Error closing threads");
            logger.trace(e.getStackTrace());
        }
        SingletonFactory.getSingleton(FileReader.class).close();
        ORMConfig.sessionFactory.close();
    }
}
