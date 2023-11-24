package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.logicalUnit.threading.Threads;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
public class AppCloser {

    private static final Logger logger = LogManager.getLogger(AppCloser.class);

    public AppCloser() {

    }

    public void close() {
        logger.debug("AppCloser");
        SingletonFactory.getSingleton(Threads.class).close();
        ORMConfig.sessionFactory.close();
    }
}
