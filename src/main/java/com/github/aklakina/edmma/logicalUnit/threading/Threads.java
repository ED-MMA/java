package com.github.aklakina.edmma.logicalUnit.threading;

import com.github.aklakina.edmma.base.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Singleton
public class Threads {

    private static final Logger logger = LogManager.getLogger(Threads.class);
    private final ArrayList<RegisteredThread> threads = new ArrayList<>();

    public Threads() {

    }

    public void registerThread(RegisteredThread thread) {
        logger.debug("Registering thread " + thread.getName());
        threads.add(thread);
    }

    public void removeThread(RegisteredThread thread) {
        logger.debug("Removing thread " + thread.getName());
        threads.remove(thread);
    }

    public synchronized void close() {
        logger.debug("Closing threads");
        while (!threads.isEmpty()) {
            try {
                logger.debug("Notifying thread " + threads.get(0).getName());
                threads.get(0).exit();
                threads.get(0).join();
            } catch (InterruptedException e) {
                logger.error("Error joining thread " + threads.get(0).getName());
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
            }
        }
        /*threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("Error joining thread " + thread.getName());
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
            }
        });*/
    }

    public void joinThreadByName(String name) {
        threads.stream().filter(thread -> thread.getName().equals(name)).forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.error("Error joining thread " + thread.getName());
                logger.error("Error: " + e.getMessage());
                logger.trace(e.getStackTrace());
            }
        });
    }

}
