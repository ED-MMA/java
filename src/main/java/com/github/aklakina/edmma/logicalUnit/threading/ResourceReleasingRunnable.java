package com.github.aklakina.edmma.logicalUnit.threading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract class representing an infinite loop based runnable that releases resources.
 * This runnable has a registered thread and can wait for thread notification and perform an action on thread notification.
 */
public abstract class ResourceReleasingRunnable implements Runnable {

    private static final Logger logger = LogManager.getLogger(ResourceReleasingRunnable.class);

    protected RegisteredThread thread;

    /**
     * Default constructor for creating a resource releasing runnable.
     */
    protected ResourceReleasingRunnable() {
    }

    /**
     * Method to release resources.
     * This method can be overridden by subclasses to provide specific resource releasing functionality.
     */
    public void releaseResources() {
    }

    /**
     * Method to wait for thread notification.
     * This method will notify all threads and then wait.
     *
     * @throws InterruptedException if any thread has interrupted the current thread
     */
    protected void waitForThreadNotify() throws InterruptedException {
        synchronized (RegisteredThread.currentThread()) {
            RegisteredThread.currentThread().notifyAll();
            RegisteredThread.currentThread().wait();
        }
    }

    /**
     * Method to run the runnable.
     * This method sets the current thread, waits for thread notification and performs an action on thread notification while the current thread should continue.
     */
    @Override
    public void run() {
        thread = RegisteredThread.currentThread();
        try {
            while (RegisteredThread.currentThread().shouldContinue()) {
                waitForThreadNotify();
                actionOnThreadNotify();
            }
        } catch (InterruptedException e) {
            logger.debug("Thread " + Thread.currentThread().getName() + " interrupted");
        }
    }

    /**
     * Method to perform an action on thread notification.
     * This method can be overridden by subclasses to provide specific action functionality.
     */
    public void actionOnThreadNotify() {
    }
}