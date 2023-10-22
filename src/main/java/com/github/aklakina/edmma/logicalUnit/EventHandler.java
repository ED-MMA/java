package com.github.aklakina.edmma.logicalUnit;

import com.github.aklakina.edmma.base.Singleton;
import com.github.aklakina.edmma.events.Event;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class EventHandler implements Runnable {

    private static final Logger logger = LogManager.getLogger(EventHandler.class);

    public boolean shouldExit = false;

    private final LinkedBlockingQueue<Event> eventsToProcess = new LinkedBlockingQueue<>();

    public boolean addEvent(Event event) {
        logger.debug("New event: " + event.getClass().getName());
        return eventsToProcess.offer(event);
    }

    // Create a thread pool for event processing
    //private final ExecutorService threadPool = Executors.newCachedThreadPool();

    @Override
    public void run() {
        do {
            Event event = null;
            try {
                event = eventsToProcess.take();
            } catch (InterruptedException e) {
                shouldExit = true;
                break;
            }
            event.run();

        } while (!shouldExit);
    }
}
