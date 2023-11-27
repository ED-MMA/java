package com.github.aklakina.edmma.logicalUnit.threading;

import com.github.aklakina.edmma.base.Singleton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Singleton class for managing threads.
 * This class registers, removes, closes, and joins threads.
 */
@Singleton
public class Threads {

    private static final Logger logger = LogManager.getLogger(Threads.class);
    private final ArrayList<RegisteredThread> threads = new ArrayList<>();

    /**
     * Default constructor for creating a Threads instance.
     */
    public Threads() {

    }

    /**
     * Method to register a thread.
     * This method adds the thread to the threads list and logs the action.
     * If the thread is already registered, this method will log a warning and return without registering the thread.
     *
     * @param thread the thread to be registered
     */
    public void registerThread(RegisteredThread thread) {
        if (threads.contains(thread)) {
            logger.warn("Thread " + thread.getName() + " already registered!");
            return;
        }
        logger.debug("Registering thread " + thread.getName());
        threads.add(thread);
    }

    /**
     * Method to remove a thread.
     * This method ends the thread and removes it from the threads list.
     *
     * @param thread the thread to be removed
     */
    void removeThread(RegisteredThread thread) {
        logger.debug("Removing thread " + thread.getName());
        threads.remove(thread);
    }

    /**
     * Method to close all threads.
     * This method exits all threads in the threads list and logs the actions.
     */
    public synchronized void close() {
        logger.debug("Closing threads");
        while (!threads.isEmpty()) {
            logger.debug("Notifying thread " + threads.get(0).getName());
            threads.get(0).exit();

        }
    }

    /**
     * Method to get the threads list.
     *
     * @return the threads list
     */
    public ArrayList<RegisteredThread> getThreads() {
        return threads;
    }

    /**
     * Get thread by name.
     *
     * @param name the name of the thread
     * @return the thread with the specified name or null if no such thread exists
     */
    public RegisteredThread getThreadByName(String name) {
        for (RegisteredThread thread : threads) {
            if (thread.getName().equals(name)) {
                return thread;
            }
        }
        return null;
    }
}