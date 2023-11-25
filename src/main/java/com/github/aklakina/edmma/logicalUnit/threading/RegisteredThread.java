package com.github.aklakina.edmma.logicalUnit.threading;

import com.github.aklakina.edmma.base.SingletonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class representing a registered thread.
 * This thread is registered to a singleton factory and has a closer method.
 */
public class RegisteredThread extends Thread {

    private static final Logger logger = LogManager.getLogger(RegisteredThread.class);
    private CloserMethods closerMethod = CloserMethods.NOTIFY;
    private boolean shouldExit = false;
    private ResourceReleasingRunnable runnable;

    /**
     * Method to check if the thread is waiting.
     *
     * @return true if the thread is waiting, false otherwise
     */
    public boolean isWaiting() {
        return this.getState() == State.WAITING;
    }

    /**
     * Constructor for creating a registered thread with a runnable and default closer method.
     *
     * @param runnable the runnable to be run by the thread
     */
    public RegisteredThread(Runnable runnable) {
        super(runnable);
    }

    /**
     * Constructor for creating a registered thread with a runnable and specified closer method.
     *
     * @param runnable the runnable to be run by the thread
     * @param closerMethod the method to close the thread
     */
    public RegisteredThread(Runnable runnable, CloserMethods closerMethod) {
        super(runnable);
        setCloserMethod(closerMethod);
    }

    /**
     * Constructor for creating a registered thread with a resource releasing runnable and default closer method.
     *
     * @param runnable the resource releasing runnable to be run by the thread
     */
    public RegisteredThread(ResourceReleasingRunnable runnable) {
        super(runnable);
        this.runnable = runnable;
    }

    /**
     * Constructor for creating a registered thread with a resource releasing runnable and specified closer method.
     *
     * @param runnable the resource releasing runnable to be run by the thread
     * @param closerMethod the method to close the thread
     */
    public RegisteredThread(ResourceReleasingRunnable runnable, CloserMethods closerMethod) {
        super(runnable);
        this.runnable = runnable;
        setCloserMethod(closerMethod);
    }

    /**
     * Method to get the current thread.
     *
     * @return the current thread
     */
    public static RegisteredThread currentThread() {
        return (RegisteredThread) Thread.currentThread();
    }

    /**
     * Method to set the closer method.
     *
     * @param closerMethod the method to close the thread
     */
    public void setCloserMethod(CloserMethods closerMethod) {
        this.closerMethod = closerMethod;
    }

    /**
     * Method to check if the thread should continue.
     *
     * @return true if the thread should not exit, false otherwise
     */
    public boolean shouldContinue() {
        return !shouldExit;
    }

    /**
     * Method to exit the thread.
     * This method sets the shouldExit flag to true and executes the closer method.
     */
    public void exit() {
        shouldExit = true;
        try {
            closerMethod.execute(this);
        } catch (InterruptedException e) {
            logger.error("Thread " + this.getName() + " interrupted while closing");
        }
        SingletonFactory.getSingleton(Threads.class).removeThread(this);
    }

    /**
     * Method to run the thread.
     * This method logs the start and end of the thread, runs the super method, releases resources if runnable is not null, and removes the thread from the singleton factory.
     */
    @Override
    public void run() {
        try {
            SingletonFactory.getSingleton(Threads.class).registerThread(this);
            logger.debug("Thread " + this.getName() + " started");
            if (runnable != null) {
                runnable.thread = this;
                runnable.run();
                runnable.releaseResources();
            } else {
                super.run();
            }
        } catch (Exception e) {
            logger.error("Error in thread " + this.getName());
            logger.error("Error: " + e.getMessage());
            logger.trace(e.getStackTrace());
        } finally {
            logger.debug("Thread " + this.getName() + " finished");
            SingletonFactory.getSingleton(Threads.class).removeThread(this);
        }
    }

    /**
     * Method to set the name of the thread.
     *
     * @param name the name to set
     * @return the thread with the set name
     */
    public RegisteredThread setNamed(String name) {
        this.setName(name);
        return this;
    }

}