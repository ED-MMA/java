package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.base.SingletonFactory;
import com.github.aklakina.edmma.events.Event;
import com.github.aklakina.edmma.logicalUnit.threading.RegisteredThread;
import com.github.aklakina.edmma.logicalUnit.threading.ResourceReleasingRunnable;
import com.github.aklakina.edmma.logicalUnit.threading.Threads;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Singleton class responsible for handling events.
 * It extends ResourceReleasingRunnable to release resources after the event is processed.
 */
@Singleton
public class EventHandler extends ResourceReleasingRunnable {

    // Logger instance for this class
    private static final Logger logger = LogManager.getLogger(EventHandler.class);
    // Queue to store events to be processed
    private final LinkedBlockingQueue<Event> eventsToProcess = new LinkedBlockingQueue<>();
    // Flag to indicate if the handler should exit
    public boolean shouldExit = false;

    /**
     * Waits for events to be processed.
     * @throws InterruptedException if the waiting thread is interrupted.
     */
    public void waitForEvents() throws InterruptedException {
        if (eventsToProcess.isEmpty()) return;
        synchronized (this) {
            this.wait();
        }
    }

    /**
     * Adds an event to the queue.
     * @param event The event to be added.
     * @return true if the event is added successfully, false otherwise.
     */
    public boolean addEvent(Event event) {
        logger.debug("New event: " + event.getClass().getName());
        return eventsToProcess.offer(event);
    }

    /**
     * Checks if the event queue is empty.
     * @return true if the event queue is empty, false otherwise.
     */
    public boolean isEmpty() {
        return eventsToProcess.isEmpty();
    }

    /**
     * Runs the event handler.
     * It processes events from the queue until the shouldExit flag is set to true.
     */
    @Override
    public void run() {
        do {
            Event event = null;
            try {
                if (eventsToProcess.isEmpty()) {
                    synchronized (this) {
                        this.notifyAll();
                    }
                }
                event = eventsToProcess.take();
            } catch (InterruptedException e) {
                shouldExit = true;
                break;
            }
            event.run();

        } while (!shouldExit);
    }

}