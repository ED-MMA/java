package com.github.aklakina.edmma.logicalUnit.threading;

import com.github.aklakina.edmma.base.SingletonFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisteredThread extends Thread {

    private static final Logger logger = LogManager.getLogger(RegisteredThread.class);
    CloserMethods closerMethod = CloserMethods.NOTIFY;
    private boolean shouldExit = false;
    private ResourceReleasingRunnable runnable;

    public RegisteredThread(Runnable runnable) {
        super(runnable);
        SingletonFactory.getSingleton(Threads.class).registerThread(this);
    }

    public RegisteredThread(Runnable runnable, CloserMethods closerMethod) {
        super(runnable);
        setCloserMethod(closerMethod);
        SingletonFactory.getSingleton(Threads.class).registerThread(this);
    }

    public RegisteredThread(ResourceReleasingRunnable runnable) {
        super(runnable);
        this.runnable = runnable;
        SingletonFactory.getSingleton(Threads.class).registerThread(this);
    }

    public RegisteredThread(ResourceReleasingRunnable runnable, CloserMethods closerMethod) {
        super(runnable);
        this.runnable = runnable;
        setCloserMethod(closerMethod);
        SingletonFactory.getSingleton(Threads.class).registerThread(this);
    }

    public static RegisteredThread currentThread() {
        return (RegisteredThread) Thread.currentThread();
    }

    public void setCloserMethod(CloserMethods closerMethod) {
        this.closerMethod = closerMethod;
    }

    public boolean shouldContinue() {
        return !shouldExit;
    }

    public void exit() {
        shouldExit = true;
        closerMethod.execute(this);
    }

    @Override
    public void run() {
        logger.debug("Thread " + this.getName() + " started");
        super.run();
        if (runnable != null)
            runnable.releaseResources();
        logger.debug("Thread " + this.getName() + " finished");
        SingletonFactory.getSingleton(Threads.class).removeThread(this);
    }

    public RegisteredThread setNamed(String name) {
        this.setName(name);
        return this;
    }

}
