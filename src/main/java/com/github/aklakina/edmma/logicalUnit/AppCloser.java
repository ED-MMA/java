package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.database.ORMConfig;
import com.github.aklakina.edmma.machineInterface.FileReader;
import com.github.aklakina.edmma.machineInterface.WatchDir;
import org.hibernate.SessionFactory;

@Singleton
public class AppCloser {
    public AppCloser() {
        System.out.println("AppCloser");
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
            System.out.println("Error joining threads");
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        SingletonFactory.getSingleton(FileReader.class).close();
        ORMConfig.sessionFactory.close();
    }
}
