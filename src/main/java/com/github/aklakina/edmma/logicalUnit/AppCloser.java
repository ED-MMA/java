package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.logicalUnit.threading.Threads;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Singleton class responsible for closing the application.
 * It closes the threads and the session factory.
 */
@Singleton
public class AppCloser {

    // Logger instance for this class
    private static final Logger logger = LogManager.getLogger(AppCloser.class);

    /**
     * Default constructor.
     */
    public AppCloser() {

    }

    /**
     * Closes the application.
     * It logs the closing process, closes the threads and the session factory.
     */
    public void close() {
        // Log the closing process
        logger.debug("AppCloser");
        // Close the threads
        SingletonFactory.getSingleton(Threads.class).close();
        // Close the session factory
        ORMConfig.sessionFactory.close();
    }
}