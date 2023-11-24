package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.events.Event;
import com.github.aklakina.edmma.logicalUnit.threading.ResourceReleasingRunnable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class EventHandler extends ResourceReleasingRunnable {
    private static final Logger logger = LogManager.getLogger(EventHandler.class);
    private final LinkedBlockingQueue<Event> eventsToProcess = new LinkedBlockingQueue<>();
    public boolean shouldExit = false;

    public void waitForEvents() throws InterruptedException {
        synchronized (this) {
            this.wait();
        }
    }

    public boolean addEvent(Event event) {
        logger.debug("New event: " + event.getClass().getName());
        return eventsToProcess.offer(event);
    }

    public boolean isEmpty() {
        return eventsToProcess.isEmpty();
    }

    // Create a thread pool for event processing
    //private final ExecutorService threadPool = Executors.newCachedThreadPool();

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
