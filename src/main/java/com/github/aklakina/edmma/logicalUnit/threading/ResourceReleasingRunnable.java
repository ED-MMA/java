package com.github.aklakina.edmma.logicalUnit.threading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ResourceReleasingRunnable implements Runnable {

    private static final Logger logger = LogManager.getLogger(ResourceReleasingRunnable.class);

    protected RegisteredThread thread;

    protected ResourceReleasingRunnable() {
    }

    public void releaseResources() {
    }

    protected void waitForThreadNotify() throws InterruptedException {
        synchronized (this) {
            this.notifyAll();
            this.wait();
        }
    }

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

    public void actionOnThreadNotify() {
    }
}
